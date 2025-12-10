// file: src/main/java/iuh/fit/se/configs/OpenApiConfig.java
package iuh.fit.se.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Healthcare Store API")
                        .version("v1")
                        .description("API documentation for Healthcare Store Backend"));
    }
}
