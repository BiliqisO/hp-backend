package com.biliqis.hafsahs_place.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hafsah's Place API")
                        .description("REST API for Hafsah's Place — Lagos-based fashion e-commerce platform. " +
                                "Authenticate via POST /api/auth/login, then use the returned JWT token as a Bearer token.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Hafsah's Place")
                                .email("dev@hafsahsplace.com")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT obtained from POST /api/auth/login")));
    }
}
