package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos para el ecosistema de turismo
                .requestMatchers("/v1/vuelos/buscar").permitAll()
                .requestMatchers("/v1/vuelos/reservar").permitAll()
                .requestMatchers("/v1/vuelos/reservas/*/confirmar").permitAll()
                .requestMatchers("/v1/vuelos/reservas/*").permitAll() // DELETE para cancelar
                .requestMatchers("/v1/vuelos/*").permitAll() // GET para consultar vuelo específico
                
                // Endpoint de autenticación
                .requestMatchers("/v1/auth/login").permitAll()
                
                // H2 Console (solo para desarrollo)
                .requestMatchers("/h2-console/**").permitAll()
                
                // Todos los endpoints administrativos requieren autenticación
                .requestMatchers("/v1/admin/**").authenticated()
                
                // Cualquier otra request
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions().disable()) // Para H2 console
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}