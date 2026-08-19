package com.acme.salarymanagement.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class SecurityConfig {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Value("${HR_USERNAME}")
    private String hrUsername;

    @Value("${HR_PASSWORD}")
    private String hrPassword;

    public SecurityConfig(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Cookie-based session auth keeps CSRF protection enabled.
                // Spring sends XSRF-TOKEN as a readable cookie, and Angular sends it back
                // on unsafe requests as the X-XSRF-TOKEN header.
                // Login/logout are ignored because they create or clear the session itself.
                .csrf(csrf -> csrf
                        // Enables Spring Security's SPA CSRF handling for the XSRF-TOKEN
                        // cookie and X-XSRF-TOKEN header used by Angular.
                        .spa()
                        .ignoringRequestMatchers("/api/auth/login", "/api/auth/logout"))
                .authorizeHttpRequests(auth -> auth
                        // Login must be public so the HR manager can create a session.
                        .requestMatchers("/api/auth/login").permitAll()
                        // Allow Spring Boot error responses to be returned instead of being hidden by
                        // auth.
                        .requestMatchers("/error").permitAll()
                        // All salary management APIs require a valid authenticated session.
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                writeSecurityErrorResponse(request, response, HttpServletResponse.SC_UNAUTHORIZED,
                                        "Please login to access this resource"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeSecurityErrorResponse(request, response, HttpServletResponse.SC_FORBIDDEN,
                                        "You do not have permission to access this resource")));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private void writeSecurityErrorResponse(HttpServletRequest request, HttpServletResponse response, int status,
            String message) throws IOException {
        if (!hasHandler(request)) {
            writeErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Resource not found");
            return;
        }

        writeErrorResponse(response, status, message);
    }

    private boolean hasHandler(HttpServletRequest request) {
        try {
            return requestMappingHandlerMapping.getHandler(request) != null;
        } catch (Exception exception) {
            return true;
        }
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        // Predefined HR account loaded from application properties or environment
        // variables. Spring Security stores this user in memory and compares login
        // requests against it.
        return new InMemoryUserDetailsManager(
                User.withUsername(hrUsername)
                        .password(passwordEncoder.encode(hrPassword))
                        .roles("HR")
                        .build());
    }

    @Bean
    // Provides password hashing and matching for the in-memory HR user. Spring
    // Security uses it to compare raw login password with the encoded stored
    // password.
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    // Exposes Spring Security's configured AuthenticationManager so AuthService can
    // inject it and validate login requests with
    // authenticationManager.authenticate(...).
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
