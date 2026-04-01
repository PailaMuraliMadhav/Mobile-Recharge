package com.example.rechargeservice.service;

import com.example.rechargeservice.config.RabbitMQConfig;
import com.example.rechargeservice.dto.*;
import com.example.rechargeservice.entity.Recharge;
import com.example.rechargeservice.enums.RechargeStatus;
import com.example.rechargeservice.exceptions.BadRequestException;
import com.example.rechargeservice.exceptions.NotFoundException;
import com.example.rechargeservice.feignclients.OperatorClient;
import com.example.rechargeservice.feignclients.PaymentClient;
import com.example.rechargeservice.repository.RechargeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RechargeServiceTest {

    @Mock private RechargeRepository rechargeRepository;
    @Mock private OperatorClient operatorClient;
    @Mock private PaymentClient paymentClient;
    @Mock private RabbitTemplate rabbitTemplate;
    @Mock private ModelMapper modelMapper;

    @InjectMocks private RechargeService rechargeService;

    private RechargeRequest request;
    private OperatorResponse operatorResponse;
    private PlanResponse planResponse;
    private PaymentResponse paymentResponse;
    private Recharge dummyRecharge;

    @BeforeEach
    void setUp() {
        request = new RechargeRequest();
        request.setUserId(1L);
        request.setOperatorId(10L);
        request.setPlanId(100L);
        request.setMobileNumber("9876543210");

        operatorResponse = new OperatorResponse();
        operatorResponse.setId(10L);
        operatorResponse.setIsActive(true);

        planResponse = new PlanResponse();
        planResponse.setId(100L);
        planResponse.setPrice(BigDecimal.valueOf(199.00));
        planResponse.setIsActive(true);

        paymentResponse = new PaymentResponse();
        paymentResponse.setTransactionId("TXN-123");
        paymentResponse.setStatus(RechargeStatus.SUCCESS);

        dummyRecharge = new Recharge();
        dummyRecharge.setId(1L);
        dummyRecharge.setMobileNumber("9876543210");
        dummyRecharge.setStatus(RechargeStatus.PENDING);
    }

    // --- processRecharge Tests ---

    @Test
    void testProcessRecharge_Success() {
        when(operatorClient.getOperatorById(10L)).thenReturn(operatorResponse);
        when(operatorClient.getPlanById(100L)).thenReturn(planResponse);
        when(rechargeRepository.existsByUserIdAndMobileNumberAndPlanIdAndStatus(anyLong(), anyString(), anyLong(), eq(RechargeStatus.SUCCESS))).thenReturn(false);
        when(rechargeRepository.save(any(Recharge.class))).thenReturn(dummyRecharge);
        when(paymentClient.processPayment(any(PaymentRequest.class))).thenReturn(paymentResponse);
        when(modelMapper.map(any(), eq(RechargeResponse.class))).thenReturn(new RechargeResponse());

        RechargeResponse result = rechargeService.processRecharge(request);

        assertNotNull(result);
        verify(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.RECHARGE_EXCHANGE), eq(RabbitMQConfig.RECHARGE_ROUTING_KEY), any(Recharge.class));
    }

    @Test
    void testProcessRecharge_OperatorInactive() {
        operatorResponse.setIsActive(false);
        when(operatorClient.getOperatorById(10L)).thenReturn(operatorResponse);

        assertThrows(BadRequestException.class, () -> rechargeService.processRecharge(request));
    }

    @Test
    void testProcessRecharge_PlanInactive() {
        when(operatorClient.getOperatorById(10L)).thenReturn(operatorResponse);
        planResponse.setIsActive(false);
        when(operatorClient.getPlanById(100L)).thenReturn(planResponse);

        assertThrows(BadRequestException.class, () -> rechargeService.processRecharge(request));
    }

    @Test
    void testProcessRecharge_AlreadyActive() {
        when(operatorClient.getOperatorById(10L)).thenReturn(operatorResponse);
        when(operatorClient.getPlanById(100L)).thenReturn(planResponse);
        when(rechargeRepository.existsByUserIdAndMobileNumberAndPlanIdAndStatus(anyLong(), anyString(), anyLong(), eq(RechargeStatus.SUCCESS))).thenReturn(true);

        assertThrows(BadRequestException.class, () -> rechargeService.processRecharge(request));
    }

    @Test
    void testProcessRecharge_PaymentClientThrowsException() {
        when(operatorClient.getOperatorById(anyLong())).thenReturn(operatorResponse);
        when(operatorClient.getPlanById(anyLong())).thenReturn(planResponse);
        when(rechargeRepository.save(any())).thenReturn(dummyRecharge);
        when(paymentClient.processPayment(any())).thenThrow(new RuntimeException("Network Error"));

        assertThrows(BadRequestException.class, () -> rechargeService.processRecharge(request));
        assertEquals(RechargeStatus.FAILED, dummyRecharge.getStatus());
    }

    @Test
    void testProcessRecharge_PaymentStatusNotSuccess() {
        when(operatorClient.getOperatorById(anyLong())).thenReturn(operatorResponse);
        when(operatorClient.getPlanById(anyLong())).thenReturn(planResponse);
        when(rechargeRepository.save(any())).thenReturn(dummyRecharge);

        paymentResponse.setStatus(RechargeStatus.FAILED); // Force failure status
        when(paymentClient.processPayment(any())).thenReturn(paymentResponse);

        assertThrows(BadRequestException.class, () -> rechargeService.processRecharge(request));
        assertEquals(RechargeStatus.FAILED, dummyRecharge.getStatus());
    }

    // --- Fallback & Callback Tests ---

    @Test
    void testPaymentFallback() {
        RechargeResponse response = rechargeService.paymentFallback(request, new RuntimeException("Circuit Breaker Open"));
        assertEquals(RechargeStatus.FAILED, response.getStatus());
        assertEquals("9876543210", response.getMobileNumber());
    }

    @Test
    void testHandleOperatorCallback_Success() {
        when(rechargeRepository.findById(1L)).thenReturn(Optional.of(dummyRecharge));

        rechargeService.handleOperatorCallback(1L, "SUCCESS");

        assertEquals(RechargeStatus.SUCCESS, dummyRecharge.getStatus());
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Recharge.class));
    }

    @Test
    void testHandleOperatorCallback_NotFound() {
        when(rechargeRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> rechargeService.handleOperatorCallback(99L, "SUCCESS"));
    }

    // --- List & History Tests ---

    @Test
    void testGetAllRecharges() {
        when(rechargeRepository.findAll()).thenReturn(List.of(dummyRecharge));
        when(modelMapper.map(any(), eq(RechargeResponse.class))).thenReturn(new RechargeResponse());

        List<RechargeResponse> result = rechargeService.getAllRecharges();
        assertEquals(1, result.size());
    }

    @Test
    void testGetRechargeHistory() {
        when(rechargeRepository.findByUserId(1L)).thenReturn(List.of(dummyRecharge));
        when(modelMapper.map(any(), eq(RechargeResponse.class))).thenReturn(new RechargeResponse());

        List<RechargeResponse> result = rechargeService.getRechargeHistory(1L);
        assertEquals(1, result.size());
    }
}