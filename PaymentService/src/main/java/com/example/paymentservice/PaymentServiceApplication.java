package com.example.paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: PaymentServiceApplication
 * DESCRIPTION:
 *   Spring Boot application entry point for the Payment microservice.
 *   Enables Eureka service discovery for registration with the service registry.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
