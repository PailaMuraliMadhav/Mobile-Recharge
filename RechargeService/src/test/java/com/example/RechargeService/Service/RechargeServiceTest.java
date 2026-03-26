package com.example.RechargeService.Service;

import java.math.BigDecimal;

import com.example.RechargeService.Config.RabbitMQConfig;
import com.example.RechargeService.Dto.*;
import com.example.RechargeService.Entity.Recharge;
import com.example.RechargeService.Enums.RechargeStatus;
import com.example.RechargeService.Exceptions.BadRequestException;
import com.example.RechargeService.Exceptions.NotFoundException;
import com.example.RechargeService.FeignClients.OperatorClient;
import com.example.RechargeService.FeignClients.PaymentClient;
import com.example.RechargeService.Repository.RechargeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RechargeServiceTest {

    @Mock
    private RechargeRepository rechargeRepository;

    @Mock
    private OperatorClient operatorClient;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RechargeService rechargeService;

    private RechargeRequest request;
    private OperatorResponse operatorResponse;
    private PlanResponse planResponse;
    private PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        request = new RechargeRequest();
        request.setUserId(1L);
        request.setOperatorId(2L);
        request.setPlanId(3L);
        request.setMobileNumber("9876543210");

        operatorResponse = new OperatorResponse();
        operatorResponse.setId(2L);
        operatorResponse.setIsActive(true);

        planResponse = new PlanResponse();
        planResponse.setId(3L);
        planResponse.setPrice(BigDecimal.valueOf(199.00));
        planResponse.setIsActive(true);

        paymentResponse = new PaymentResponse();
        paymentResponse.setTransactionId("txn-12345");
        paymentResponse.setStatus(RechargeStatus.SUCCESS);
    }

    @Test
    void testProcessRecharge_Success() {
        when(operatorClient.getOperatorById(request.getOperatorId())).thenReturn(operatorResponse);
        when(operatorClient.getPlanById(request.getPlanId())).thenReturn(planResponse);

        when(rechargeRepository.existsByUserIdAndMobileNumberAndPlanIdAndStatus(
                request.getUserId(), request.getMobileNumber(), request.getPlanId(), RechargeStatus.SUCCESS))
                .thenReturn(false);

        Recharge dummyRecharge = new Recharge();
        dummyRecharge.setId(10L);
        when(rechargeRepository.save(any(Recharge.class))).thenReturn(dummyRecharge);

        when(paymentClient.processPayment(any(PaymentRequest.class))).thenReturn(paymentResponse);

        RechargeResponse finalResponse = new RechargeResponse();
        finalResponse.setId(10L);
        when(modelMapper.map(dummyRecharge, RechargeResponse.class)).thenReturn(finalResponse);

        RechargeResponse result = rechargeService.processRecharge(request);

        assertNotNull(result);
        assertEquals(RechargeStatus.SUCCESS, dummyRecharge.getStatus());
        assertEquals("txn-12345", dummyRecharge.getTransactionId());

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(RabbitMQConfig.RECHARGE_EXCHANGE),
                eq(RabbitMQConfig.RECHARGE_ROUTING_KEY),
                any(Recharge.class)
        );
        verify(rechargeRepository, times(2)).save(any(Recharge.class));
    }

    @Test
    void testProcessRecharge_OperatorNotFound() {
        when(operatorClient.getOperatorById(request.getOperatorId())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> rechargeService.processRecharge(request));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    void testProcessRecharge_DuplicateRecharge() {
        when(operatorClient.getOperatorById(request.getOperatorId())).thenReturn(operatorResponse);
        when(operatorClient.getPlanById(request.getPlanId())).thenReturn(planResponse);

        when(rechargeRepository.existsByUserIdAndMobileNumberAndPlanIdAndStatus(
                request.getUserId(), request.getMobileNumber(), request.getPlanId(), RechargeStatus.SUCCESS))
                .thenReturn(true);

        assertThrows(BadRequestException.class, () -> rechargeService.processRecharge(request));
    }

    @Test
    void testProcessRecharge_PaymentFailed() {
        when(operatorClient.getOperatorById(request.getOperatorId())).thenReturn(operatorResponse);
        when(operatorClient.getPlanById(request.getPlanId())).thenReturn(planResponse);

        when(rechargeRepository.existsByUserIdAndMobileNumberAndPlanIdAndStatus(
                request.getUserId(), request.getMobileNumber(), request.getPlanId(), RechargeStatus.SUCCESS))
                .thenReturn(false);

        Recharge dummyRecharge = new Recharge();
        dummyRecharge.setId(10L);
        when(rechargeRepository.save(any(Recharge.class))).thenReturn(dummyRecharge);

        when(paymentClient.processPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException("Payment Gateway Timeout"));

        assertThrows(BadRequestException.class, () -> rechargeService.processRecharge(request));

        assertEquals(RechargeStatus.FAILED, dummyRecharge.getStatus());
    }
}
