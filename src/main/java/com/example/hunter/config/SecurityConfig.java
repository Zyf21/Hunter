package com.example.hunter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration

public class SecurityConfig {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()            // если вы не используете формы — отключаем CSRF
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()   // все запросы разрешены без аутентификации
                );
        return http.build();
    }
}
