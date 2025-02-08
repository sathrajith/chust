package com.sp.service.provider.config;

import com.sp.service.provider.filter.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Disable CSRF protection
                .cors().and() // Enable CORS
                .authorizeHttpRequests()
                .requestMatchers(
                        "/api/auth/register", // Allow registration
                        "/api/auth/login", // Allow login
                        "/api/auth/refresh", // Allow token refresh
                        "/swagger-ui/**", // Allow Swagger UI
                        "/v3/api-docs/**", // Allow Swagger API docs
                        "/swagger-resources/**", // Allow Swagger resources
                        "/webjars/**" // Allow WebJars (used by Swagger)
                ).permitAll() // Permit all access to the above endpoints
                .requestMatchers("/api/providers/**").hasAuthority("ROLE_PROVIDER") // Secure provider endpoints
                .requestMatchers("/api/bookings/**").hasAuthority("ROLE_CUSTOMER")
                .requestMatchers("/api/users/**").hasAuthority("ROLE_USER")// Secure customer endpoints
                .anyRequest().authenticated() // Require authentication for all other endpoints
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Use stateless sessions

        // Add JWT filter before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}