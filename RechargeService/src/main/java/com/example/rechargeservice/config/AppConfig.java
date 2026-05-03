package com.example.rechargeservice.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: AppConfig
 * DESCRIPTION:
 *   Spring configuration class for the Recharge Service.
 *   Declares the ModelMapper bean used for DTO-entity mapping.
 */
@Configuration
public class AppConfig {

    /* ================================================================
     * METHOD: modelMapper
     * DESCRIPTION:
     *   Creates and registers a ModelMapper bean for object mapping between DTOs and entities.
     * ================================================================ */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
