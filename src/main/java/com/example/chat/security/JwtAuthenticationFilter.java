package com.example.chat.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.http.Cookie;

import java.io.IOException;

/**
 * 모든 요청에 대해 JWT를 확인하는 필터.
 * 유효한 JWT가 존재할 경우 SecurityContext에 인증 정보를 설정합니다.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class); // 로깅 추가

    private final JwtTokenProvider tokenProvider;  // 토큰 생성 및 검증을 위한 JwtTokenProvider
    private final UserDetailsService userDetailsService; // 사용자 정보를 로드하기 위한 UserDetailsService

    // JwtTokenProvider와 UserDetailsService를 주입 받습니다.
    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, UserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * 모든 요청에서 JWT 토큰을 확인하고, 유효한 토큰일 경우 인증 정보를 설정하는 핵심 메서드
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 1. 요청에서 JWT 토큰을 추출
            String token = getAccessTokenFromCookies(request.getCookies());

            // 2. 토큰의 유효성을 검사하고, 유효한 경우 사용자 정보를 SecurityContext에 설정
            if (token != null && tokenProvider.validateToken(token)) {
                String username = tokenProvider.getUsernameFromToken(token); // 토큰에서 사용자 이름 추출

                // 3. UserDetailsService를 이용하여 사용자 정보 로드
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 4. 인증 토큰을 생성하여 SecurityContext에 설정
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 5. SecurityContext에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);

                LOGGER.info("인증 성공: 사용자 이름 - {}, 요청 URI: {}", username, request.getRequestURI());


            }

        } catch (Exception e) {

            LOGGER.error("JWT 인증 과정 중 오류 발생: {}", e.getMessage()); // 예외 로그

        }
        // 다음 필터로 요청을 전달
        filterChain.doFilter(request, response);
    }

    /**
     * 요청 헤더에서 JWT 토큰을 추출합니다.
     * 토큰은 "Authorization" 헤더에 "Bearer "로 시작하는 형식으로 제공됩니다.
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후의 토큰 부분만 반환
        }
        return null;
    }

    /**
     * 쿠키에서 Access Token 추출
     * @param cookies
     * @return null
     */
    private String getAccessTokenFromCookies(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    logger.info("Access token found in cookies: " + cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        logger.warn("No access token found in cookies");
        return null;
    }


}
