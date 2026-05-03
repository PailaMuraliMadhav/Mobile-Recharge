package com.example.rechargeservice.service;

import com.example.rechargeservice.config.RabbitMQConfig;
import com.example.rechargeservice.dto.*;
import com.example.rechargeservice.entity.Recharge;
import com.example.rechargeservice.enums.RechargeStatus;
import com.example.rechargeservice.exceptions.BadRequestException;
import com.example.rechargeservice.exceptions.NotFoundException;
import com.example.rechargeservice.feignclients.OperatorClient;
import com.example.rechargeservice.feignclients.PaymentClient;
import com.example.rechargeservice.feignclients.UserClient;
import com.example.rechargeservice.repository.RechargeRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: RechargeService
 * DESCRIPTION:
 *   Business logic layer for mobile recharge processing.
 *   Coordinates operator validation, payment processing via Feign clients,
 *   recharge persistence, and asynchronous notification dispatch through RabbitMQ.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RechargeService {

    private static final String STATUS_SUCCESS = "SUCCESS";

    private final RechargeRepository rechargeRepository;
    private final OperatorClient operatorClient;
    private final PaymentClient paymentClient;
    private final UserClient userClient;
    private final RabbitTemplate rabbitTemplate;
    private final ModelMapper modelMapper;

    /* ================================================================
     * METHOD: recordCancelledRecharge
     * DESCRIPTION:
     *   Records a recharge attempt that was cancelled before payment as a FAILED record.
     *   Bypasses the circuit breaker and payment service entirely.
     * ================================================================ */
    @Transactional
    public RechargeResponse recordCancelledRecharge(RechargeRequest request) {
        OperatorResponse operator = operatorClient.getOperatorById(request.getOperatorId());
        PlanResponse planResponse = operatorClient.getPlanById(request.getPlanId());
        UserResponse user = userClient.getUserById(request.getUserId());

        String planDescription;
        if (planResponse != null) {
            planDescription = planResponse.getDescription() != null
                    ? planResponse.getDescription()
                    : planResponse.getData();
        } else {
            planDescription = "";
        }

        Recharge recharge = Recharge.builder()
                .userId(request.getUserId())
                .operatorId(request.getOperatorId())
                .planId(request.getPlanId())
                .mobileNumber(request.getMobileNumber())
                .amount(planResponse != null ? planResponse.getPrice() : BigDecimal.ZERO)
                .operatorName(operator != null ? operator.getName() : "Unknown")
                .planName(planResponse != null ? planResponse.getName() : "Unknown")
                .planDescription(planDescription)
                .validityDays(planResponse != null ? planResponse.getValidityDays() : 0)
                .userEmail(user != null ? user.getEmail() : null)
                .paymentMode("CANCELLED")
                .status(RechargeStatus.FAILED)
                .build();

        Recharge savedRecharge = rechargeRepository.save(recharge);
        sendNotification(savedRecharge);
        return modelMapper.map(savedRecharge, RechargeResponse.class);
    }

    /* ================================================================
     * METHOD: processRecharge
     * DESCRIPTION:
     *   Validates the operator and plan, checks for duplicate active recharges,
     *   initiates payment via the payment service, and persists the final recharge status.
     *   Protected by a Resilience4j circuit breaker on the payment service call.
     * ================================================================ */
    @Transactional
    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
    public RechargeResponse processRecharge(RechargeRequest request) {

        OperatorResponse operator = operatorClient.getOperatorById(request.getOperatorId());
        if (operator == null || !operator.getIsActive()) {
            throw new BadRequestException("Operator is not available.");
        }

        PlanResponse planResponse = operatorClient.getPlanById(request.getPlanId());
        if (planResponse == null || !planResponse.getIsActive()) {
            throw new BadRequestException("This plan is no longer available.");
        }

        UserResponse user = userClient.getUserById(request.getUserId());
        String userEmail = (user != null) ? user.getEmail() : null;

//        boolean alreadyActive = rechargeRepository.existsByUserIdAndMobileNumberAndPlanIdAndStatus(
//                request.getUserId(), request.getMobileNumber(), request.getPlanId(), RechargeStatus.SUCCESS);
//
//        if (alreadyActive) {
//            throw new BadRequestException("You already have an active recharge for this plan.");
//        }

        Recharge recharge = Recharge.builder()
                .userId(request.getUserId())
                .operatorId(request.getOperatorId())
                .planId(request.getPlanId())
                .mobileNumber(request.getMobileNumber())
                .amount(planResponse.getPrice())
                .operatorName(operator.getName())
                .planName(planResponse.getName())
                .planDescription(planResponse.getDescription() != null ? planResponse.getDescription() : planResponse.getData())
                .validityDays(planResponse.getValidityDays())
                .userEmail(userEmail)
                .paymentMode(request.getPaymentMode())
                .status(RechargeStatus.PENDING)
                .build();

        recharge = rechargeRepository.save(recharge);

        // Send PENDING notification only for real payment attempts
        sendNotification(recharge);

        PaymentResponse paymentResponse;

        try {
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setRechargeId(recharge.getId());
            paymentRequest.setUserId(request.getUserId());
            paymentRequest.setAmount(planResponse.getPrice());
            paymentRequest.setPaymentMode(request.getPaymentMode());

            paymentResponse = paymentClient.processPayment(paymentRequest);

            if (paymentResponse == null) {
                throw new BadRequestException("Payment service returned empty response");
            }
            recharge.setTransactionId(paymentResponse.getTransactionId());

        } catch (Exception ex) {
            recharge.setStatus(RechargeStatus.FAILED);
            rechargeRepository.save(recharge);
            sendNotification(recharge);
            throw new BadRequestException("Payment gateway unavailable. Please try again later.");
        }

        if (paymentResponse.getStatus() == null || !paymentResponse.getStatus().toString().equalsIgnoreCase("SUCCESS")) {
            recharge.setStatus(RechargeStatus.FAILED);
            rechargeRepository.save(recharge);
            throw new BadRequestException("Payment failed.");
        }

        recharge.setStatus(RechargeStatus.SUCCESS);
        rechargeRepository.save(recharge);

        // Send SUCCESS notification
        sendNotification(recharge);

        return modelMapper.map(recharge, RechargeResponse.class);
    }

    /* ================================================================
     * METHOD: sendNotification
     * DESCRIPTION:
     *   Publishes a recharge event to the RabbitMQ exchange so the notification
     *   service can send an email to the user. Errors are logged but not propagated.
     * ================================================================ */
    private void sendNotification(Recharge recharge) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.RECHARGE_EXCHANGE,
                    RabbitMQConfig.RECHARGE_ROUTING_KEY,
                    recharge
            );
        } catch (Exception e) {
            log.error("Failed to send notification to RabbitMQ: {}", e.getMessage());
        }
    }

    /* ================================================================
     * METHOD: paymentFallback
     * DESCRIPTION:
     *   Circuit breaker fallback invoked when the payment service is unavailable or the
     *   circuit is open. Returns a FAILED recharge response without calling the payment service.
     * ================================================================ */
    public RechargeResponse paymentFallback(RechargeRequest request, Throwable t) {
        log.error("Circuit Breaker 'paymentService' is OPEN or call failed. Reason: {}", t.getMessage());

        RechargeResponse fallbackResponse = new RechargeResponse();
        fallbackResponse.setMobileNumber(request.getMobileNumber());
        fallbackResponse.setStatus(RechargeStatus.FAILED);
        return fallbackResponse;
    }

    /* ================================================================
     * METHOD: handleOperatorCallback
     * DESCRIPTION:
     *   Handles an asynchronous status callback from the operator, updating the recharge
     *   record to SUCCESS or FAILED and publishing the result to RabbitMQ.
     * ================================================================ */
    @Transactional
    public void handleOperatorCallback(Long rechargeId, String operatorStatus) {
        Recharge recharge = rechargeRepository.findById(rechargeId)
                .orElseThrow(() -> new NotFoundException("Recharge record not found: " + rechargeId));

        recharge.setStatus(STATUS_SUCCESS.equalsIgnoreCase(operatorStatus) ? RechargeStatus.SUCCESS : RechargeStatus.FAILED);
        rechargeRepository.save(recharge);

        rabbitTemplate.convertAndSend(RabbitMQConfig.RECHARGE_EXCHANGE, RabbitMQConfig.RECHARGE_ROUTING_KEY, recharge);
    }

    /* ================================================================
     * METHOD: updatePaymentStatus
     * DESCRIPTION:
     *   Updates the transaction ID, payment mode, and status of an existing recharge record
     *   after external payment verification, then dispatches a notification event.
     * ================================================================ */
    @Transactional
    public RechargeResponse updatePaymentStatus(PaymentVerificationUpdateRequest request) {
        Recharge recharge = rechargeRepository.findById(request.getRechargeId())
                .orElseThrow(() -> new NotFoundException("Recharge record not found: " + request.getRechargeId()));

        recharge.setTransactionId(request.getTransactionId());
        recharge.setPaymentMode(request.getPaymentMode());
        recharge.setStatus(STATUS_SUCCESS.equalsIgnoreCase(request.getStatus())
                ? RechargeStatus.SUCCESS
                : RechargeStatus.FAILED);

        Recharge saved = rechargeRepository.save(recharge);
        sendNotification(saved);

        return modelMapper.map(saved, RechargeResponse.class);
    }

    /* ================================================================
     * METHOD: getAllRecharges
     * DESCRIPTION:
     *   Returns a list of all recharge records across all users, intended for admin use.
     * ================================================================ */
    public List<RechargeResponse> getAllRecharges() {
        return rechargeRepository.findAll().stream()
                .map(recharge -> modelMapper.map(recharge, RechargeResponse.class)).toList();
    }

    /* ================================================================
     * METHOD: getRechargeHistory
     * DESCRIPTION:
     *   Returns the recharge history for a specific user identified by their user ID.
     * ================================================================ */
    public List<RechargeResponse> getRechargeHistory(Long userId) {
        return rechargeRepository.findByUserId(userId).stream()
                .map(recharge -> modelMapper.map(recharge, RechargeResponse.class)).toList();
    }

    /* ================================================================
     * METHOD: getAdminAnalytics
     * DESCRIPTION:
     *   Aggregates recharge data into detailed analytics for the admin dashboard.
     * ================================================================ */
    public AdminAnalyticsResponse getAdminAnalytics() {
        List<Recharge> allRecharges = rechargeRepository.findAll();
        List<Recharge> successfulRecharges = allRecharges.stream()
                .filter(r -> r.getStatus() == RechargeStatus.SUCCESS)
                .toList();

        Map<String, Long> operatorUsage = successfulRecharges.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getOperatorName() != null ? r.getOperatorName() : "Unknown",
                        Collectors.counting()));

        Map<String, Long> popularPlans = successfulRecharges.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getPlanName() != null ? r.getPlanName() : "Unknown",
                        Collectors.counting()));

        Map<String, BigDecimal> revenueByDay = successfulRecharges.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCreatedAt().toLocalDate().toString(),
                        Collectors.reducing(BigDecimal.ZERO, Recharge::getAmount, BigDecimal::add)));

        Map<String, Long> paymentModes = successfulRecharges.stream()
                .filter(r -> !"CANCELLED".equalsIgnoreCase(r.getPaymentMode()))
                .collect(Collectors.groupingBy(
                        r -> r.getPaymentMode() != null ? r.getPaymentMode() : "Unknown",
                        Collectors.counting()));

        BigDecimal totalRevenue = successfulRecharges.stream()
                .map(Recharge::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return AdminAnalyticsResponse.builder()
                .operatorUsage(operatorUsage)
                .popularPlans(popularPlans)
                .revenueByDay(revenueByDay)
                .paymentModeDistribution(paymentModes)
                .totalRecharges((long) successfulRecharges.size())
                .totalRevenue(totalRevenue)
                .build();
    }
}
