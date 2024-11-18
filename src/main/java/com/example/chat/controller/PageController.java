package com.example.chat.controller;

import com.example.chat.dto.UserSignupDto;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pages")
public class PageController {

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new UserSignupDto());
        return "signup";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
