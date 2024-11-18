package com.example.chat.security;

import com.example.chat.exception.InvalidJwtException;
import com.example.chat.exception.TokenExpiredException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

/**
 *   JwtTokenProvider : JWT 토큰을 생성하고 검증하는 핵심 클래스
 *   1. JWT 토큰 생성: 사용자 정보(주로 username)를 기반으로 JWT 토큰을 생성
 *   2. JWT 토큰에서 사용자 정보 추출: 토큰을 파싱하여 사용자 이름(혹은 ID 등)을 추출
 *   3. JWT 토큰 검증: 토큰의 유효성, 만료 여부, 서명 검증을 수행
 */
@Component
public class JwtTokenProvider {

    // 3. JWT 토큰의 유효성 검사를 단순 Print가 아니라 Logger로 처리
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-expiration}")
    private long JWT_ACCESS_TOKEN_EXPIRATION;

    @Value("${jwt.refresh-token-expiration}")
    private long JWT_REFRESH_TOKEN_EXPIRATION;

    private Key key;

    // 객체 초기화 시 Secret Key를 Base64 인코딩하여 Key 객체로 설정
    @PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 1-1. JWT 액세스 토큰 생성 메서드
     * 사용자 이름을 기반으로 JWT를 생성합니다.
     */
    public String generateAccessToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_ACCESS_TOKEN_EXPIRATION);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256) // HS256 방식으로 설정
                .compact();
    }

    /**
     * 1-2. JWT 리프레시 토큰 생성 메서드
     * 사용자 이름을 기반으로 JWT를 생성합니다.
     */
    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_REFRESH_TOKEN_EXPIRATION);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256) // HS256 방식으로 설정
                .compact();
    }

    /**
     * 2. JWT 토큰에서 사용자 이름 추출
     * 토큰의 내용을 파싱하여 사용자 이름을 추출합니다.
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * 3. JWT 토큰의 유효성 검사
     * 토큰의 서명, 만료 여부, 형식 등을 검증합니다.
     */
    public boolean validateToken(String token) {

//        try {
//            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//            return true;
//        } catch (SecurityException | MalformedJwtException e) {
//            System.out.println("잘못된 JWT 서명입니다.");
//        } catch (ExpiredJwtException e) {
//            System.out.println("만료된 JWT 토큰입니다.");
//        } catch (UnsupportedJwtException e) {
//            System.out.println("지원되지 않는 JWT 토큰입니다.");
//        } catch (IllegalArgumentException e) {
//            System.out.println("JWT 토큰이 잘못되었습니다.");
//        }
//        return false;
//    }
        // Logger : 로그 레벨(info, warn, error)으로 메세지의 중요도 구분 가능
        // 로그 메세지를 파일, 콘솔, 원격서버 등 다양한 출력대상으로 쉽게 전송 가능 (Print보다 유지보수 향상)
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            LOGGER.error("잘못된 JWT 서명입니다: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            LOGGER.warn("만료된 JWT 토큰입니다: {}", e.getMessage());
            throw new TokenExpiredException("토큰이 만료되었습니다. Refresh Token을 사용하세요.");
        } catch (UnsupportedJwtException e) {
            LOGGER.error("지원되지 않는 JWT 토큰입니다: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("JWT 토큰이 잘못되었습니다: {}", e.getMessage());
            throw new InvalidJwtException("유효하지 않은 토큰입니다.");
        }
        return false;
    }
}
