package com.example.user.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder(){ //평문을 암호화해서 해싱처리
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println(">>>>>>>>> user filter chain ") ; 
        http.csrf(csrf -> csrf.disable() ) // Cross-Site Request Forgery(사이트 위변조)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) ) ;
        return http.build() ; 
        
    }
}
