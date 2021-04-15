package org.gtf.valorantlineup.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearer-jwt",
                            new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                            .in(SecurityScheme.In.HEADER).name("Authorization")))
                .info(new Info().title("Valorant Lineup")
                            .description("An application developed to provide lineup guide for Valorant agents.")
                            .version("v0.0.1")
                            .license(new License().name("GTF Discord").url("https://http://discord.gg/yfYhBnD")))
                .addSecurityItem(
                new SecurityRequirement().addList("bearer-jwt", Arrays.asList("read", "write")));
    }
}
