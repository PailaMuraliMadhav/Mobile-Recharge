package com.example.operatorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: OperatorServiceApplication
 * DESCRIPTION:
 *   Spring Boot application entry point for the Operator microservice.
 *   Enables Eureka service discovery for registration with the service registry.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class OperatorServiceApplication {

    /* ================================================================
     * METHOD: main
     * DESCRIPTION:
     *   Bootstraps and starts the Operator Service Spring Boot application.
     * ================================================================ */
    public static void main(String[] args) {
        SpringApplication.run(OperatorServiceApplication.class, args);
    }
}
