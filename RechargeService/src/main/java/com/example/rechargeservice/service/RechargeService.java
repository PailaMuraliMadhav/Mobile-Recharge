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

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RechargeService {

    private final RechargeRepository rechargeRepository;
    private final OperatorClient operatorClient;
    private final PaymentClient paymentClient;
    private final RabbitTemplate rabbitTemplate;
    private final ModelMapper modelMapper;

    @Transactional
    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
    public RechargeResponse processRecharge(RechargeRequest request) {

        OperatorResponse operator = operatorClient.getOperatorById(request.getOperatorId());
        if (operator == null || !operator.getIsActive()) {
            throw new BadRequestException("Operator is not available.");
        }

        PlanResponse plan = operatorClient.getPlanById(request.getPlanId());
        if (plan == null || !plan.getIsActive()) {
            throw new BadRequestException("This plan is no longer available.");
        }

        boolean alreadyActive = rechargeRepository.existsByUserIdAndMobileNumberAndPlanIdAndStatus(
                request.getUserId(), request.getMobileNumber(), request.getPlanId(), RechargeStatus.SUCCESS);

        if (alreadyActive) {
            throw new BadRequestException("You already have an active recharge for this plan.");
        }

        Recharge recharge = Recharge.builder()
                .userId(request.getUserId())
                .operatorId(request.getOperatorId())
                .planId(request.getPlanId())
                .mobileNumber(request.getMobileNumber())
                .amount(plan.getPrice())
                .status(RechargeStatus.PENDING)
                .build();

        recharge = rechargeRepository.save(recharge);

        PaymentResponse paymentResponse;
        try {
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setRechargeId(recharge.getId());
            paymentRequest.setUserId(request.getUserId());
            paymentRequest.setAmount(plan.getPrice());

            paymentResponse = paymentClient.processPayment(paymentRequest);

            if (paymentResponse == null) {
                throw new BadRequestException("Payment service returned empty response");
            }
            recharge.setTransactionId(paymentResponse.getTransactionId());

        } catch (Exception ex) {
            recharge.setStatus(RechargeStatus.FAILED);
            rechargeRepository.save(recharge);
            throw new BadRequestException("Payment gateway unavailable. Please try again later.");
        }

        if (!"SUCCESS".equalsIgnoreCase(paymentResponse.getStatus().name())) {
            recharge.setStatus(RechargeStatus.FAILED);
            rechargeRepository.save(recharge);
            throw new BadRequestException("Payment failed.");
        }

        recharge.setStatus(RechargeStatus.SUCCESS);
        rechargeRepository.save(recharge);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.RECHARGE_EXCHANGE,
                RabbitMQConfig.RECHARGE_ROUTING_KEY,
                recharge
        );

        return modelMapper.map(recharge, RechargeResponse.class);
    }

    /**
     * Fallback method for Circuit Breaker
     * This runs when the Payment Service is slow or down.
     */
    public RechargeResponse paymentFallback(RechargeRequest request, Throwable t) {
        log.error("Circuit Breaker 'paymentService' is OPEN or call failed. Reason: {}", t.getMessage());

        // Return a response that tells the user to try again later
        // without crashing the whole application
        RechargeResponse fallbackResponse = new RechargeResponse();
        fallbackResponse.setMobileNumber(request.getMobileNumber());
        fallbackResponse.setStatus(RechargeStatus.FAILED);
        return fallbackResponse;
    }

    @Transactional
    public void handleOperatorCallback(Long rechargeId, String operatorStatus) {
        Recharge recharge = rechargeRepository.findById(rechargeId)
                .orElseThrow(() -> new NotFoundException("Recharge record not found: " + rechargeId));

        recharge.setStatus("SUCCESS".equalsIgnoreCase(operatorStatus) ? RechargeStatus.SUCCESS : RechargeStatus.FAILED);
        rechargeRepository.save(recharge);

        rabbitTemplate.convertAndSend(RabbitMQConfig.RECHARGE_EXCHANGE, RabbitMQConfig.RECHARGE_ROUTING_KEY, recharge);
    }

    public List<RechargeResponse> getAllRecharges() {
        return rechargeRepository.findAll().stream()
                .map(recharge -> modelMapper.map(recharge, RechargeResponse.class)).toList();
    }

    public List<RechargeResponse> getRechargeHistory(Long userId) {
        return rechargeRepository.findByUserId(userId).stream()
                .map(recharge -> modelMapper.map(recharge, RechargeResponse.class)).toList();
    }
}