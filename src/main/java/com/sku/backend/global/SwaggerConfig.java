package com.sku.backend.global;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl(contextPath);
        server.setDescription("Production Server");

        return new OpenAPI()
                .addServersItem(server)
                .info(new Info()
                        .title("2026 소웨&전컴 공모전 백엔드 API")
                        .version("1.0")
                        .description("5nel-day1 백엔드 API 명세서입니다."));
    }

    @Bean
    public GroupedOpenApi customGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("all-api")
                .pathsToMatch("/**")
                .build();
    }
}