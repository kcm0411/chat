package com.example.chat.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
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

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long JWT_EXPIRATION;

    private Key key;

    // 객체 초기화 시 Secret Key를 Base64 인코딩하여 Key 객체로 설정
    @PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 1. JWT 토큰 생성 메서드
     * 사용자 이름을 기반으로 JWT를 생성합니다.
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

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
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            System.out.println("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            System.out.println("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            System.out.println("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}
