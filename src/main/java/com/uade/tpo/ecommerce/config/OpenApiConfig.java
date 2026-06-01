package com.uade.tpo.ecommerce.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Ecommerce API")
                .version("1.0.0")
                .description("API REST para plataforma de ecommerce - TPO Grupo 4")
                .contact(new Contact()
                    .name("Grupo 4 - TPO")
                    .url("https://github.com/"))
                .license(new License()
                    .name("Apache 2.0")))
            .components(new Components()
                .addSecuritySchemes("bearer-jwt",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Ingresa tu token JWT obtenido de /auth/login")))
            .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
