package com.example.chat.service;

import com.example.chat.dto.UserSignupDto;
import com.example.chat.entity.User;
import com.example.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String signup(UserSignupDto userDto) {

        String encodedPassword = passwordEncoder.encode(userDto.getPassword());

        // 정적 팩토리 메서드
        User user = User.fromDto(userDto,encodedPassword);

        userRepository.save(user);

        return "회원가입 성공";

    }
}
