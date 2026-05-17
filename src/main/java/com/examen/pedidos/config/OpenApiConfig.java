package com.examen.pedidos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8081}")
    private String port;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ms-pedidos API")
                        .description("Microservicio de gestión de pedidos. El total se calcula " +
                                "automáticamente en el backend: total = cantidad × precioUnitario.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Miguel Sánchez")
                                .email("sanchezsanchezmiguelivan@gmail.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Local"),
                        new Server().url("https://ms-pedidos.onrender.com").description("Render (producción)")
                ));
    }
}
