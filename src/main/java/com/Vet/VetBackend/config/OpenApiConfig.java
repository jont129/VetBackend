package com.Vet.VetBackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI api() {
                return new OpenAPI()
                        .info(new Info()
                                .title("VetBackend API")
                                .description("Servicios, Tratamientos y Facturación")
                                .version("v1"));
        }

        // Ver todo junto
        @Bean
        public GroupedOpenApi all() {
                return GroupedOpenApi.builder()
                        .group("all")
                        .packagesToScan("com.Vet.VetBackend")   // raíz del proyecto
                        .pathsToMatch("/api/**")                // o "/**" si quieres probar sin filtro
                        .build();
        }

        // (Opcional) grupos por módulo para el selector de la UI
        @Bean
        public GroupedOpenApi servicios() {
                return GroupedOpenApi.builder()
                        .group("servicios")
                        .packagesToScan("com.Vet.VetBackend.servicios.web.controller")
                        .pathsToMatch("/api/**")
                        .build();
        }

        @Bean
        public GroupedOpenApi tratamientos() {
                return GroupedOpenApi.builder()
                        .group("tratamientos")
                        .packagesToScan("com.Vet.VetBackend.tratamientos.web.controller")
                        .pathsToMatch("/api/**")
                        .build();
        }

        @Bean
        public GroupedOpenApi facturacion() {
                return GroupedOpenApi.builder()
                        .group("facturacion")
                        .packagesToScan("com.Vet.VetBackend.facturacion.web.controller")
                        .pathsToMatch("/api/**")
                        .build();
        }
}