package com.example.NotificationService.Service;


import com.example.NotificationService.Dto.RechargeEvent;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void sendNotification(RechargeEvent event) {

        System.out.println(" Recharge Notification");

        System.out.println("Mobile: " + event.getMobileNumber());
        System.out.println("Amount: ₹" + event.getAmount());
        System.out.println("Status: " + event.getStatus());
        System.out.println("Transaction ID: " + event.getTransactionId());

        System.out.println("Notification sent successfully!");
    }
}