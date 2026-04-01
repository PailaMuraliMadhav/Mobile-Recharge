package com.example.notificationservice.consumer;


import com.example.notificationservice.dto.RechargeEvent;
import com.example.notificationservice.service.NotificationService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RechargeConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = "recharge.queue")
    public void receiveRechargeEvent(RechargeEvent event) {

        log.info("Received Recharge Event for mobile: {}", event.getMobileNumber());
        notificationService.sendNotification(event);
    }
}
