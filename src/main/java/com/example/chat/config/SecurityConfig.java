package com.example.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**").permitAll()  // 로그인 페이지와 정적 리소스는 누구나 접근 허용
                        .anyRequest().authenticated()  // 그 외의 모든 요청은 인증 필요
                )
                .formLogin(form -> form
                        .loginPage("/login")  // 사용자 정의 로그인 페이지 경로
                        .defaultSuccessUrl("/chat", true)  // 로그인 성공 후 이동할 경로
                        .permitAll()  // 로그인 페이지 접근 허용
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")  // 로그아웃 성공 후 이동할 경로
                );

        return http.build();
    }
}
