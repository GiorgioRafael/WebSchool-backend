// src/main/java/com/example/escola/config/OpenApiConfig.java
package com.example.escola.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Imports Adicionados
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // O nome do esquema de segurança (pode ser qualquer string)
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Escola API")
                        .version("v0.0.1")
                        .description("API do projeto Escola")
                        .contact(new Contact().name("Equipe").email("contato@example.com"))
                )
                // Adiciona o requisito de segurança a todas as operações
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                // Define o componente de segurança
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP) // Tipo HTTP
                                                .scheme("bearer") // Esquema "bearer"
                                                .bearerFormat("JWT") // Formato "JWT"
                                )
                );
    }
}