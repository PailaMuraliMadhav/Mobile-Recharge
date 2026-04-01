package com.example.operatorservice.config;

import com.example.operatorservice.dto.PlanResponse;
import com.example.operatorservice.entity.Plan;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper(){

        ModelMapper mapper = new ModelMapper();

        mapper.typeMap(Plan.class, PlanResponse.class).addMappings(m -> {
            m.map(src -> src.getOperator().getId(), PlanResponse::setOperatorId);
            m.map(src -> src.getOperator().getName(), PlanResponse::setOperatorName);
        });

        return mapper;
    }
}
