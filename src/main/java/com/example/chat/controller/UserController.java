package com.example.chat.controller;

import com.example.chat.dto.UserDto;
import com.example.chat.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // 현재 인증된 사용자 정보(CustomUserDetails)에서 사용자 정보를 가져옵니다.
        UserDto userDto = new UserDto(userDetails.getUsername(), userDetails.getName());
        return ResponseEntity.ok(userDto);
    }
}
