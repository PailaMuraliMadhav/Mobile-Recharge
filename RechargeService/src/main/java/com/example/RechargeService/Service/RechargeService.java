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

    private final RechargeRepository    rechargeRepository;
    private final OperatorClient        operatorClient;
    private final PaymentClient         paymentClient;
    private final RabbitTemplate        rabbitTemplate;
    private final ModelMapper           modelMapper;

    @Transactional
    public RechargeResponse processRecharge(RechargeRequest request) {

        // Validate operator exists and is active
        OperatorResponse operator =
                operatorClient.getOperatorById(request.getOperatorId());

        if (operator == null || !operator.getIsActive()) {
            throw new BadRequestException(
                    "Operator is not available. Please try another operator.");
        }

        // Validate plan exists and is still offered
        PlanResponse plan =
                operatorClient.getPlanById(request.getPlanId());

        if (plan == null || !plan.getIsActive()) {
            throw new BadRequestException(
                    "This plan is no longer available. Please choose another plan.");
        }

        // Prevent recharge if same plan is already active on this number
        boolean alreadyActive =
                rechargeRepository
                        .existsByUserIdAndMobileNumberAndPlanIdAndStatus(
                                request.getUserId(),
                                request.getMobileNumber(),
                                request.getPlanId(),
                                RechargeStatus.SUCCESS);

        if (alreadyActive) {
            throw new BadRequestException(
                    "You already have an active recharge for this plan. " +
                            "Please wait for it to expire before recharging again.");
        }

        // Save recharge order as PENDING before payment
        Recharge recharge = Recharge.builder()
                .userId(request.getUserId())
                .operatorId(request.getOperatorId())
                .planId(request.getPlanId())
                .mobileNumber(request.getMobileNumber())
                .amount(plan.getPrice())
                .status(RechargeStatus.PENDING)
                .build();

        recharge = rechargeRepository.save(recharge);

        // Call payment service to deduct amount from user
        PaymentResponse paymentResponse;

        try {
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setRechargeId(recharge.getId());
            paymentRequest.setUserId(request.getUserId());
            paymentRequest.setAmount(plan.getPrice());

            paymentResponse = paymentClient.processPayment(paymentRequest);
            recharge.setTransactionId(paymentResponse.getTransactionId());

        } catch (Exception ex) {
            // Mark failed if payment gateway is unreachable
            recharge.setStatus(RechargeStatus.FAILED);
            rechargeRepository.save(recharge);
            throw new BadRequestException(
                    "Payment gateway unavailable. Please try again. " +
                            "No amount has been deducted.");
        }

        // Mark recharge failed and stop if payment was unsuccessful
        if (!"SUCCESS".equalsIgnoreCase(paymentResponse.getStatus().name())) {
            recharge.setStatus(RechargeStatus.FAILED);
            rechargeRepository.save(recharge);
            throw new BadRequestException(
                    "Payment failed. Please check your payment method " +
                            "and try again. If amount was deducted, " +
                            "it will be refunded within 5-7 business days.");
        }

        // Keep status as PENDING until operator confirms activation
        recharge.setStatus(RechargeStatus.PENDING);
        rechargeRepository.save(recharge);

        // Publish recharge event to operator via RabbitMQ for async activation
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.RECHARGE_EXCHANGE,
                RabbitMQConfig.RECHARGE_ROUTING_KEY,
                recharge
        );

        // Return response informing user that activation is in progress
        RechargeResponse response =
                modelMapper.map(recharge, RechargeResponse.class);

        return response;
    }

    // Update recharge status based on operator network callback
    @Transactional
    public void handleOperatorCallback(Long rechargeId, String operatorStatus) {

        Recharge recharge = rechargeRepository.findById(rechargeId)
                .orElseThrow(() -> new NotFoundException(
                        "Recharge not found: " + rechargeId));

        if ("SUCCESS".equalsIgnoreCase(operatorStatus)) {
            // Activate plan and notify user on success
            recharge.setStatus(RechargeStatus.SUCCESS);
        } else {
            // Trigger refund and notify user on failure
            recharge.setStatus(RechargeStatus.FAILED);
        }

        rechargeRepository.save(recharge);

        // Publish final status to notification service
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.RECHARGE_EXCHANGE,
                RabbitMQConfig.RECHARGE_ROUTING_KEY,
                recharge
        );
    }

    // Fetch all recharges for admin view
    public List<RechargeResponse> getAllRecharges() {
        return rechargeRepository.findAll()
                .stream()
                .map(recharge -> modelMapper.map(recharge, RechargeResponse.class))
                .toList();
    }

    // Fetch recharge history for a specific user
    public List<RechargeResponse> getRechargeHistory(Long userId) {
        return rechargeRepository.findByUserId(userId)
                .stream()
                .map(recharge -> modelMapper.map(recharge, RechargeResponse.class))
                .toList();
    }
}