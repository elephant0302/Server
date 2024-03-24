package com.hyunn.capstone.controller.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .contact(new Contact().name("JRGB").email("hyuntae9912@naver.com"))
            .title("JRGB API")
            .description("생성형 AI와 3d print를 활용한 사용자 맞춤 3D 악세사리 출력 서비스")
            .version("1.0.0"));
  }
}

