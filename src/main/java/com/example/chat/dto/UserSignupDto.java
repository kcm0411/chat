package com.example.chat.dto;

import lombok.Getter;
import lombok.Setter;

// User 객체를 회원가입할 때 model 로 넘겨줘야 하는데 User의 기본생성자 접근자가 Protected여서 Dto를 통해서 전달

@Getter
@Setter
public class UserSignupDto {

    private String username;
    private String password;
    private String name;

}
