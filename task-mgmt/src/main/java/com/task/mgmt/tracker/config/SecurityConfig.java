package com.task.mgmt.tracker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import com.task.mgmt.tracker.security.jwt.AuthEntryPointJwt;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private TokenValidationFilter tokenValidationFilter;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    private static final String[] PUBLIC_ENDPOINTS = {
        "/api/v1/auth/**",
        "/api/v1/otp/**",
        "/public/**",
        "/external/**",
        "/actuator/health",
        "/ws/**",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/webjars/**"
        
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
//            .cors(cors -> cors.disable())
            .csrf(csrf -> csrf.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(form -> form.disable())
            .logout(logout -> logout.disable())
            .exceptionHandling(exception -> 
                exception.authenticationEntryPoint(unauthorizedHandler)
            )
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                .anyRequest().permitAll()
            );

        http.addFilterBefore(tokenValidationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    

}