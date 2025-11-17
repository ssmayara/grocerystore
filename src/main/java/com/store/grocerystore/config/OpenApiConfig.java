package com.store.grocerystore.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI groceryStoreOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Grocery Store API")
            .description("API for grocery orders and discounts")
            .version("v1"));
  }
}
