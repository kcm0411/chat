package com.example.chat.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    // BCryptPasswordEncoder 빈 생성: 비밀번호 해싱
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/signup", "/login", "/css/**", "/js/**").permitAll() // 공개 리소스
                        .requestMatchers("/admin/**").hasRole("ADMIN") // ADMIN 권한만 접근 가능
                        .requestMatchers("/chat/**").hasAnyRole("USER", "ADMIN") // USER와 ADMIN 권한 모두 접근 가능
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("loginId") // 로그인 폼의 아이디 필드 이름
                        .passwordParameter("password") // 로그인 폼의 비밀번호 필드 이름
                        .successHandler(customAuthenticationSuccessHandler()) // 커스텀 성공 핸들러
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS) // 세션을 항상 생성
                )
                .securityContext(securityContext -> securityContext
                        .requireExplicitSave(false) // SecurityContext 자동 저장
                );

        return http.build();
    }

    // 커스텀 로그인 성공 핸들러
    private AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                // 세션에서 저장된 요청 확인
                SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
                String redirectUrl = (savedRequest != null) ? savedRequest.getRedirectUrl() : null;

                // 저장된 요청이 없는 경우 기본 URL로 설정
                if (redirectUrl == null || redirectUrl.contains("continue")) {
                    redirectUrl = "/chat";
                }

                // 리다이렉트 수행
                response.sendRedirect(redirectUrl);
            }
        };
    }

//    인메모리 계정 : 삭제 (회원가입 기능 구현)
//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user1 = User.withDefaultPasswordEncoder()
//                .username("user1")
//                .password("qwe1")
//                .roles("USER")
//                .build();
//
//        UserDetails user2 = User.withDefaultPasswordEncoder()
//                .username("user2")
//                .password("qwe2")
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(user1, user2);
//    }
}
