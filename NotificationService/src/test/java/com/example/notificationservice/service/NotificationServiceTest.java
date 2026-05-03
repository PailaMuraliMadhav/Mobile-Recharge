package com.example.notificationservice.service;

import com.example.notificationservice.dto.RechargeEvent;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: NotificationServiceTest
 * DESCRIPTION:
 *   Unit tests for NotificationService covering success, failure, pending,
 *   missing email, and null field scenarios.
 */
class NotificationServiceTest {

    private JavaMailSender mailSender;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        mailSender = Mockito.mock(JavaMailSender.class);
        notificationService = new NotificationService(mailSender);
        ReflectionTestUtils.setField(notificationService, "fromEmail", "test@gmail.com");

        MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    private RechargeEvent buildEvent(String status) {
        RechargeEvent e = new RechargeEvent();
        e.setId(1L);
        e.setMobileNumber("9876543210");
        e.setAmount(BigDecimal.valueOf(199.00));
        e.setStatus(status);
        e.setTransactionId("TXN-12345");
        e.setUserEmail("user@gmail.com");
        e.setOperatorName("Jio");
        e.setPlanName("Basic Plan");
        e.setPlanDescription("1GB/day, Unlimited calls");
        e.setValidityDays(28);
        return e;
    }

    @Test
    void sendNotification_success_sendsEmail() {
        assertDoesNotThrow(() -> notificationService.sendNotification(buildEvent("SUCCESS")));
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendNotification_failed_sendsEmail() {
        assertDoesNotThrow(() -> notificationService.sendNotification(buildEvent("FAILED")));
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendNotification_pending_skipsEmail() {
        notificationService.sendNotification(buildEvent("PENDING"));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void sendNotification_missingEmail_skipsEmail() {
        RechargeEvent e = buildEvent("SUCCESS");
        e.setUserEmail(null);
        notificationService.sendNotification(e);
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void sendNotification_emptyEmail_skipsEmail() {
        RechargeEvent e = buildEvent("SUCCESS");
        e.setUserEmail("  ");
        notificationService.sendNotification(e);
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void sendNotification_mailSenderThrows_doesNotPropagate() {
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("SMTP error"));
        assertDoesNotThrow(() -> notificationService.sendNotification(buildEvent("SUCCESS")));
    }

    @Test
    void sendNotification_nullOperatorName_doesNotThrow() {
        RechargeEvent e = buildEvent("SUCCESS");
        e.setOperatorName(null);
        assertDoesNotThrow(() -> notificationService.sendNotification(e));
    }

    @Test
    void sendNotification_nullPlanDescription_doesNotThrow() {
        RechargeEvent e = buildEvent("SUCCESS");
        e.setPlanDescription(null);
        assertDoesNotThrow(() -> notificationService.sendNotification(e));
    }

    @Test
    void sendNotification_nullTransactionId_doesNotThrow() {
        RechargeEvent e = buildEvent("FAILED");
        e.setTransactionId(null);
        assertDoesNotThrow(() -> notificationService.sendNotification(e));
    }

    @Test
    void sendNotification_nullValidityDays_doesNotThrow() {
        RechargeEvent e = buildEvent("SUCCESS");
        e.setValidityDays(null);
        assertDoesNotThrow(() -> notificationService.sendNotification(e));
    }
}
