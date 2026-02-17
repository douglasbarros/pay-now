package com.ezycollect.server.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration.
 */
@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI().info(new Info().title("PayNow Payment API").version("1.0.0")
                                .description("Payment application API with webhook support")
                                .contact(new Contact().name("Douglas Barros")
                                                .email("dbs.douglas@gmail.com"))
                                .license(new License().name("Apache 2.0").url(
                                                "https://www.apache.org/licenses/LICENSE-2.0.html")))
                                .servers(List.of(new Server().url("http://localhost:8081")
                                                .description("Local development server"),
                                                new Server().url("http://207.244.255.183:8081")
                                                                .description("Homologation server"),
                                                new Server().url(
                                                                "https://paynow-api.dbsinfosolutions.com/")
                                                                .description("Production server")));
        }
}
