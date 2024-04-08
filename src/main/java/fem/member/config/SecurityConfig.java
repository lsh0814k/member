package fem.member.config;

import fem.member.application.JwtService;
import fem.member.application.jwt.JwtProperties;
import fem.member.infrastructure.filter.JwtAuthenticationFilter;
import fem.member.infrastructure.filter.JwtAuthorizationFilter;
import fem.member.infrastructure.filter.JwtLogoutFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final AuthenticationConfiguration authenticationConfiguration;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .authorizeHttpRequests(authorizeRequests -> {
                    authorizeRequests.requestMatchers("/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**",
                                    "/api/members", "/api/members/**/verify/**", "/api/reissue")
                            .permitAll().anyRequest().authenticated();
                });
        http
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), jwtProperties, jwtService))
                .addFilterBefore(new JwtAuthorizationFilter(jwtProperties, jwtService), JwtAuthenticationFilter.class)
                .addFilterBefore(new JwtLogoutFilter(jwtProperties, jwtService), LogoutFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
