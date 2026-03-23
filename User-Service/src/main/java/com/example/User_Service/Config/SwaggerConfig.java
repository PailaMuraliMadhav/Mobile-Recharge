package com.example.User_Service.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

    @Configuration
    public class SwaggerConfig {

        @Bean
        public OpenAPI openAPI() {
            return new OpenAPI()
                    .info(new Info()
                            .title("OmniCharge — User Service API")
                            .description("User registration, login, profile and admin management")
                            .version("v1.0.0"))

                    // ── This adds the Authorize button with Bearer JWT ─────
                    .addSecurityItem(new SecurityRequirement()
                            .addList("bearerAuth"))

                    .components(new Components()
                            .addSecuritySchemes("bearerAuth",
                                    new SecurityScheme()
                                            .name("bearerAuth")
                                            .type(SecurityScheme.Type.HTTP)
                                            .scheme("bearer")
                                            .bearerFormat("JWT")
                                            .description("Paste your JWT token here. Get it from POST /api/users/login")
                            )
                    );
        }
}
