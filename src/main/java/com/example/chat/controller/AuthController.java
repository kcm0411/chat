package com.example.chat.controller;

import com.example.chat.constant.ResponseMessages;
import com.example.chat.dto.*;
import com.example.chat.entity.RefreshToken;
import com.example.chat.repository.UserRepository;
import com.example.chat.security.JwtTokenProvider;
import com.example.chat.service.RefreshTokenService;
import com.example.chat.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 인증 요청을 처리하는 컨트롤러
 * 로그인 요청을 받아 JWT 토큰을 발급합니다.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager; // 사용자 인증을 처리하는 AuthenticationManager
    private final JwtTokenProvider tokenProvider; // JWT 토큰을 생성하는 JwtTokenProvider
    private final RefreshTokenService refreshTokenService; // RefreshToken 관리
    private final UserRepository userRepository;
    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class); // Log 관리

    public AuthController(AuthenticationManager authenticationManager
                        , JwtTokenProvider tokenProvider
                        , RefreshTokenService refreshTokenService
                        , UserRepository userRepository
                        , UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signupSubmit(@RequestBody UserSignupDto userDto){

        // UserDto 정보를 받아와서 아이디 중복 체크
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(ResponseMessages.SIGNUP_DUPLICATE_USERNAME);
        }

        // User 기본생성자가 Protected 이기 때문에, 정적 팩토리 메서드를 활용하여 회원가입 구현하는 서비스로직
        userService.signup(userDto);
        return ResponseEntity.ok(ResponseMessages.SIGNUP_SUCCESS);

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
        String accessToken = tokenProvider.generateAccessToken(loginRequest.getUsername());
        String refreshToken = tokenProvider.generateRefreshToken(loginRequest.getUsername());

        // 4. 기존에 있던 refreshToken 삭제 후 생성된 refreshToken 저장
        refreshTokenService.deleteByUsername(loginRequest.getUsername());
        RefreshToken savedToken = refreshTokenService.createRefreshToken(loginRequest.getUsername(), refreshToken);
        LOGGER.info("Refresh Token 저장 완료: {}", savedToken);

        // 5. 응답 바디에 JWT 토큰 포함
        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));

    }

    /**
     * Refresh Token을 사용한 Access Token 재발급
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshAccessToken(@RequestBody TokenRefreshRequest request) {

        // 1. Refresh Token 검증
        String refreshToken = request.getRefreshToken();
        Optional<RefreshToken> storedToken = refreshTokenService.findByToken(refreshToken);

        if (storedToken.isEmpty()) {
            LOGGER.warn("Refresh Token not found: {}", refreshToken);
            return ResponseEntity.status(403).body(new TokenResponse(ResponseMessages.REFRESH_TOKEN_NOT_FOUND));
        }

        if (refreshTokenService.isTokenExpired(storedToken.get())) {
            LOGGER.warn("Refresh Token expired: {}", refreshToken);
            return ResponseEntity.status(403).body(new TokenResponse(ResponseMessages.REFRESH_TOKEN_EXPIRED));
        }

        // 2. Refresh Token이 유효하면 새로운 Access Token 발급
        String username = tokenProvider.getUsernameFromToken(refreshToken);
        String newAccessToken = tokenProvider.generateAccessToken(username);

        return ResponseEntity.ok(new TokenResponse(newAccessToken));
    }
}
