package com.example.NotificationService.Service;

import java.math.BigDecimal;

import com.example.NotificationService.Dto.RechargeEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class NotificationServiceTest {

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService();
    }

    @Test
    void testSendNotification_Success() {
        // Arrange
        RechargeEvent event = new RechargeEvent();
        event.setMobileNumber("9876543210");
        event.setAmount(BigDecimal.valueOf(100.0));
        event.setStatus("SUCCESS");
        event.setTransactionId("txn-987654");

        // Act & Assert
        assertDoesNotThrow(() -> notificationService.sendNotification(event));
    }
}
