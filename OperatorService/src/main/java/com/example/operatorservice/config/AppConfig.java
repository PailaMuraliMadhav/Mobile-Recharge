package com.example.operatorservice.config;

import com.example.operatorservice.dto.PlanResponse;
import com.example.operatorservice.entity.Plan;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: AppConfig
 * DESCRIPTION:
 *   Spring configuration class for the Operator Service.
 *   Declares the ModelMapper bean with a custom mapping from Plan entity to PlanResponse DTO,
 *   including operator ID and name extraction.
 */
@Configuration
public class AppConfig {

    /* ================================================================
     * METHOD: modelMapper
     * DESCRIPTION:
     *   Creates and registers a ModelMapper bean with a custom type mapping
     *   that extracts operatorId and operatorName from the nested Operator entity.
     * ================================================================ */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.typeMap(Plan.class, PlanResponse.class).addMappings(m -> {
            m.map(src -> src.getOperator().getId(), PlanResponse::setOperatorId);
            m.map(src -> src.getOperator().getName(), PlanResponse::setOperatorName);
        });
        return mapper;
    }
}
