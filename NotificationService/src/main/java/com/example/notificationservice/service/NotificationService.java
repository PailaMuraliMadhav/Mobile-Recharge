package com.example.notificationservice.service;


import com.example.notificationservice.dto.RechargeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void sendNotification(RechargeEvent event) {

        log.info(" Recharge Notification");
        log.info("Mobile: {}", event.getMobileNumber());
        log.info("Amount: ₹{}", event.getAmount());
        log.info("Status: {}", event.getStatus());
        log.info("Transaction ID: {}", event.getTransactionId());

        log.info("Notification sent successfully!");
    }
}