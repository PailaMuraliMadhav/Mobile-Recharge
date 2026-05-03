package com.example.notificationservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: NotificationServiceApplicationTests
 * DESCRIPTION:
 *   Spring Boot context load test for the Notification Service.
 *   Uses the "test" profile to disable cloud services and use stub mail config.
 */
@SpringBootTest
@ActiveProfiles("test")
class NotificationServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
