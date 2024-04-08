package fem.member.config;

import fem.member.application.jwt.JwtProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private final JwtProperties jwtProperties;

    @Bean
    public OpenAPI openAPI() {
        String access = "Access Token";
        String refresh = "refresh Token";
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(access)
                .addList(refresh);

        SecurityScheme accessToken = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("Bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name(jwtProperties.getHeaderPrefix());

        SecurityScheme refreshToken = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name(jwtProperties.getRefreshHeaderPrefix());


        Components components = new Components()
                .addSecuritySchemes(access, accessToken)
                .addSecuritySchemes(refresh, refreshToken);

        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}
