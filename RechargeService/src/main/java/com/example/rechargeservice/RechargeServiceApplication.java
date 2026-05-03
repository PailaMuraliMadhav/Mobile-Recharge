package com.example.rechargeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: RechargeServiceApplication
 * DESCRIPTION:
 *   Spring Boot application entry point for the Recharge microservice.
 *   Enables Eureka service discovery and Feign client support for inter-service communication.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class RechargeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RechargeServiceApplication.class, args);
    }
}
