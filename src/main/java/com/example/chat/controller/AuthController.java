package com.example.chat.controller;

import com.example.chat.dto.LoginRequest;
import com.example.chat.dto.LoginResponse;
import com.example.chat.security.JwtTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 인증 요청을 처리하는 컨트롤러
 * 로그인 요청을 받아 JWT 토큰을 발급합니다.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager; // 사용자 인증을 처리하는 AuthenticationManager
    private final JwtTokenProvider tokenProvider; // JWT 토큰을 생성하는 JwtTokenProvider

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    /**
     * 로그인 요청을 처리하여 JWT 토큰을 발급하는 엔드포인트
     * @param loginRequest 사용자 로그인 정보 (username, password)
     * @return JWT 토큰
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {

        // 1. AuthenticationManager를 통해 사용자 인증 처리
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        // 2. 인증 정보를 SecurityContext에 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. 인증된 사용자 이름으로 JWT 토큰 생성
        String token = tokenProvider.generateToken(loginRequest.getUsername());

        // 4. 응답 헤더에 토큰 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token); // Authorization 헤더에 JWT 토큰 추가

        // 5. 응답 바디에 JWT 토큰 포함
        LoginResponse loginResponse = new LoginResponse(token);

        return ResponseEntity.ok()
                .headers(headers) // 헤더에 설정한 HttpHeaders 객체 추가
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginResponse); // 응답 바디로 LoginResponse 객체 반환
    }
}
