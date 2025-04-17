package com.spark.adminserver.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger 3) 配置类
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Admin Server API")
                        .description("后台管理服务接口文档")
                        .version("v0.0.1")
                        .contact(new Contact().name("Your Name/Team").url("https://your.domain").email("your.email@example.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

    // 如果需要更复杂的配置，例如添加全局请求头、安全认证等，可以在这里进一步配置
    // 参考 Springdoc OpenAPI 官方文档: https://springdoc.org/
} 