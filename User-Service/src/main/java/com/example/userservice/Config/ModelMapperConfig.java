package com.example.userservice.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: ModelMapperConfig
 * DESCRIPTION:
 *   Spring configuration class for the User Service.
 *   Declares the ModelMapper bean used for DTO-entity mapping.
 */
@Configuration
public class ModelMapperConfig {

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
