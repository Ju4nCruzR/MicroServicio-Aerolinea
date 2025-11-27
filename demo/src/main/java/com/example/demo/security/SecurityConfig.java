package com.example.demo.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // H2 Console (solo para desarrollo) - PRIMERO
                .requestMatchers("/h2-console/**").permitAll()
                
                // Endpoint de autenticación - SEGUNDO  
                .requestMatchers("/v1/auth/login", "/v1/auth/validate").permitAll()
                
                // Endpoints públicos para el ecosistema de turismo - TERCERO
                .requestMatchers("GET", "/v1/aeropuertos").permitAll()
                .requestMatchers("GET", "/v1/aeropuertos/*").permitAll()
                .requestMatchers("POST", "/v1/vuelos/buscar").permitAll()
                .requestMatchers("POST", "/v1/vuelos/reservar").permitAll() 
                .requestMatchers("PUT", "/v1/vuelos/reservas/*/confirmar").permitAll()
                .requestMatchers("GET", "/v1/vuelos/reservas/*").permitAll()
                .requestMatchers("DELETE", "/v1/vuelos/reservas/cancelar").permitAll()
                .requestMatchers("GET", "/v1/vuelos/*").permitAll()
                
                // Endpoints administrativos requieren autenticación - CUARTO  
                .requestMatchers("/v1/admin/**").authenticated()
                
                // Por defecto, todo lo demás requiere autenticación - ÚLTIMO
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())) // Para H2 console
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // Permitir todos los orígenes para desarrollo
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
        configuration.setAllowCredentials(false); // false si usamos "*" en origins, true si especificamos dominios
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}