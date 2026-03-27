package com.example.RechargeService.Service;

import com.example.RechargeService.Config.RabbitMQConfig;
import com.example.RechargeService.Dto.*;
import com.example.RechargeService.Entity.Recharge;
import com.example.RechargeService.Enums.RechargeStatus;
import com.example.RechargeService.Exceptions.BadRequestException;
import com.example.RechargeService.Exceptions.NotFoundException;
import com.example.RechargeService.FeignClients.OperatorClient;
import com.example.RechargeService.FeignClients.PaymentClient;
import com.example.RechargeService.Repository.RechargeRepository;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RechargeService {

    private final RechargeRepository rechargeRepository;
    private final OperatorClient operatorClient;
    private final PaymentClient paymentClient;
    private final RabbitTemplate rabbitTemplate;
    private final ModelMapper modelMapper;

    @Transactional
    public RechargeResponse processRecharge(RechargeRequest request) {

        OperatorResponse operator = operatorClient.getOperatorById(request.getOperatorId());

        if (operator == null || !operator.getIsActive()) {
            throw new BadRequestException("Operator is not available. Please try another operator.");
        }

        PlanResponse plan = operatorClient.getPlanById(request.getPlanId());

        if (plan == null || !plan.getIsActive()) {
            throw new BadRequestException("This plan is no longer available. Please choose another plan.");
        }

        boolean alreadyActive = rechargeRepository.existsByUserIdAndMobileNumberAndPlanIdAndStatus(
                request.getUserId(),
                request.getMobileNumber(),
                request.getPlanId(),
                RechargeStatus.SUCCESS);

        if (alreadyActive) {
            throw new BadRequestException("You already have an active recharge for this plan. Please wait for it to expire.");
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
                throw new Exception("Payment service returned empty response");
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
            throw new BadRequestException("Payment failed. Please check your balance or payment method.");
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

    @Transactional
    public void handleOperatorCallback(Long rechargeId, String operatorStatus) {

        Recharge recharge = rechargeRepository.findById(rechargeId)
                .orElseThrow(() -> new NotFoundException("Recharge record not found: " + rechargeId));

        if ("SUCCESS".equalsIgnoreCase(operatorStatus)) {
            recharge.setStatus(RechargeStatus.SUCCESS);
        } else {
            recharge.setStatus(RechargeStatus.FAILED);
        }

        rechargeRepository.save(recharge);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.RECHARGE_EXCHANGE,
                RabbitMQConfig.RECHARGE_ROUTING_KEY,
                recharge
        );
    }

    public List<RechargeResponse> getAllRecharges() {
        return rechargeRepository.findAll()
                .stream()
                .map(recharge -> modelMapper.map(recharge, RechargeResponse.class))
                .toList();
    }

    public List<RechargeResponse> getRechargeHistory(Long userId) {
        return rechargeRepository.findByUserId(userId)
                .stream()
                .map(recharge -> modelMapper.map(recharge, RechargeResponse.class))
                .toList();
    }
}