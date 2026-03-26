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

        // 1️⃣ Validate Operator
        OperatorResponse operator =
                operatorClient.getOperatorById(request.getOperatorId());

        if (operator == null) {
            throw new NotFoundException(
                    "Operator not found with id: " + request.getOperatorId());
        }

        if (!operator.getIsActive()) {
            throw new BadRequestException("Operator is inactive");
        }

        // 2️⃣ Fetch Plan
        PlanResponse plan =
                operatorClient.getPlanById(request.getPlanId());

        if (plan == null) {
            throw new NotFoundException(
                    "Plan not found with id: " + request.getPlanId());
        }

        if (!plan.getIsActive()) {
            throw new BadRequestException("Plan is inactive");
        }

        // 3️⃣ Prevent duplicate successful recharge
        if (rechargeRepository.existsByUserIdAndMobileNumberAndPlanIdAndStatus(
                request.getUserId(),
                request.getMobileNumber(),
                request.getPlanId(),
                RechargeStatus.SUCCESS)) {

            throw new BadRequestException(
                    "Recharge already completed for this mobile number with this plan");
        }

        // 4️⃣ Create Recharge Record (PENDING)
        Recharge recharge = Recharge.builder()
                .userId(request.getUserId())
                .operatorId(request.getOperatorId())
                .planId(request.getPlanId())
                .mobileNumber(request.getMobileNumber())
                .amount(plan.getPrice())
                .status(RechargeStatus.PENDING)
                .build();

        recharge = rechargeRepository.save(recharge);

        try {

            // 5️⃣ Call Payment Service
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setRechargeId(recharge.getId());
            paymentRequest.setUserId(request.getUserId());
            paymentRequest.setAmount(plan.getPrice());

            PaymentResponse paymentResponse =
                    paymentClient.processPayment(paymentRequest);

            // 6️⃣ Store Transaction ID
            recharge.setTransactionId(
                    paymentResponse.getTransactionId());

            // 7️⃣ Update Recharge Status
            if ("SUCCESS".equalsIgnoreCase(
                    paymentResponse.getStatus().name())) {

                recharge.setStatus(RechargeStatus.SUCCESS);

            } else {

                recharge.setStatus(RechargeStatus.FAILED);
            }

        } catch (Exception ex) {

            recharge.setStatus(RechargeStatus.FAILED);

            throw new BadRequestException(
                    "Payment failed: " + ex.getMessage());
        }

        rechargeRepository.save(recharge);

        // 8️⃣ Publish RabbitMQ Event
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.RECHARGE_EXCHANGE,
                RabbitMQConfig.RECHARGE_ROUTING_KEY,
                recharge
        );

        // 9️⃣ Return Response
        return modelMapper.map(recharge, RechargeResponse.class);
    }

    // Get All Recharges
    public List<RechargeResponse> getAllRecharges() {

        return rechargeRepository.findAll()
                .stream()
                .map(recharge ->
                        modelMapper.map(recharge, RechargeResponse.class))
                .toList();
    }

    // Get Recharge History By User
    public List<RechargeResponse> getRechargeHistory(Long userId) {

        return rechargeRepository.findByUserId(userId)
                .stream()
                .map(recharge ->
                        modelMapper.map(recharge, RechargeResponse.class))
                .toList();
    }
}