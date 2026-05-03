package com.example.notificationservice.consumer;


import com.example.notificationservice.dto.RechargeEvent;
import com.example.notificationservice.service.NotificationService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: RechargeConsumer
 * DESCRIPTION:
 *   RabbitMQ message consumer that listens on the recharge queue and delegates
 *   incoming recharge events to the NotificationService for email dispatch.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RechargeConsumer {

    private final NotificationService notificationService;

    /* ================================================================
     * METHOD: receiveRechargeEvent
     * DESCRIPTION:
     *   Consumes a RechargeEvent message from the RabbitMQ recharge queue,
     *   logs the mobile number, and triggers the notification sending process.
     * ================================================================ */
    @RabbitListener(queues = "recharge.queue")
    public void receiveRechargeEvent(RechargeEvent event) {

        log.info("Received Recharge Event for mobile: {}", event.getMobileNumber());
        notificationService.sendNotification(event);
    }
}
