package com.example.chat.constant;

public class ResponseMessages {

    // 회원가입 관련 메시지
    public static final String SIGNUP_SUCCESS = "회원가입 성공";
    public static final String SIGNUP_DUPLICATE_USERNAME = "이미 사용중인 아이디입니다.";

    // 로그인 관련 메시지
    public static final String LOGIN_SUCCESS = "로그인 성공";

    // Refresh Token 관련 메시지
    public static final String REFRESH_TOKEN_NOT_FOUND = "Refresh Token not found";
    public static final String REFRESH_TOKEN_EXPIRED = "Expired Refresh Token";

    // 기타 메시지
    public static final String INVALID_REQUEST = "요청이 유효하지 않습니다.";

    private ResponseMessages() {
        // 상수 클래스이므로 인스턴스화 방지
    }
}
