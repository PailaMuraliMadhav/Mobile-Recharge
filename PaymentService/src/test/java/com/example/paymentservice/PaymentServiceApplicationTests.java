package com.example.paymentservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: PaymentServiceApplicationTests
 * DESCRIPTION:
 *   Spring Boot context load test for the Payment Service.
 *   Uses the "test" profile to connect to local MySQL and disable cloud services.
 */
@SpringBootTest
@ActiveProfiles("test")
class PaymentServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
