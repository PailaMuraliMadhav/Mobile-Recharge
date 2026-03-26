package com.example.NotificationService.Consumer;


import com.example.NotificationService.Dto.RechargeEvent;
import com.example.NotificationService.Service.NotificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RechargeConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = "recharge.queue")
    public void receiveRechargeEvent(RechargeEvent event) {

        System.out.println("Received Recharge Event");

        notificationService.sendNotification(event);
    }
}
