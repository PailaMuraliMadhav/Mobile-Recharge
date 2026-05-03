package com.example.rechargeservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: SwaggerConfig
 * DESCRIPTION:
 *   Spring configuration class that sets up the OpenAPI (Swagger) documentation
 *   for the Recharge Service, routing all API calls through the API Gateway.
 */
@Configuration
public class SwaggerConfig {

    @Value("${springdoc.api-docs.servers[0].url:http://localhost:8080}")
    private String gatewayUrl;

    /* ================================================================
     * METHOD: openAPI
     * DESCRIPTION:
     *   Configures the OpenAPI specification with service info, gateway server URL,
     *   and JWT bearer authentication scheme.
     * ================================================================ */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OmniCharge — Recharge Service API")
                        .description("Recharge processing service")
                        .version("v1.0.0"))
                .servers(List.of(new Server().url(gatewayUrl).description("API Gateway")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
