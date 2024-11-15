package com.example.chat.controller;

import com.example.chat.dto.UserSignupDto;
import com.example.chat.repository.UserRepository;
import com.example.chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @GetMapping("/signup")
    public String signupForm(Model model){
        model.addAttribute("user", new UserSignupDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(@ModelAttribute UserSignupDto userDto, Model model){

        // UserDto 정보를 받아와서 아이디 중복 체크
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            model.addAttribute("errorMessage", "이미 사용중인 아이디입니다.");
            return "signup";
        }

        // User 기본생성자가 Protected 이기 때문에, 정적 팩토리 메서드를 활용하여 회원가입 구현하는 서비스로직
        return userService.signup(userDto);

    }
}
