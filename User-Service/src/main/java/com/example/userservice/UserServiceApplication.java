package com.example.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: UserServiceApplication
 * DESCRIPTION:
 *   Spring Boot application entry point for the User microservice.
 *   Enables Eureka service discovery, Feign client support for inter-service communication,
 *   and scheduled tasks for OTP cleanup.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
public class UserServiceApplication {

    /* ================================================================
     * METHOD: main
     * DESCRIPTION:
     *   Bootstraps and starts the User Service Spring Boot application.
     * ================================================================ */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
