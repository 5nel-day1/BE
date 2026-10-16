package com.sku.backend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String BEARER_AUTH_SCHEME_NAME = "bearerAuth";

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl(contextPath);
        server.setDescription("Production Server");

        SecurityScheme bearerAuthScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("로그인 응답으로 받은 accessToken을 입력해주세요.");

        return new OpenAPI()
                .addServersItem(server)
                .info(new Info()
                        .title("2026 소웨&전컴 공모전 백엔드 API")
                        .version("1.0")
                        .description("5nel-day1 백엔드 API 명세서입니다. 로그인 후 우측 상단의 Authorize 버튼을 눌러 accessToken을 입력하세요."))
                .components(new Components().addSecuritySchemes(BEARER_AUTH_SCHEME_NAME, bearerAuthScheme))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH_SCHEME_NAME));
    }

    @Bean
    public GroupedOpenApi customGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("all-api")
                .pathsToMatch("/**")
                .build();
    }
}