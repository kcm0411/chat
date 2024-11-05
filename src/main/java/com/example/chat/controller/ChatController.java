package com.example.chat.controller;

import com.example.chat.dto.ChatMessageDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {

    @GetMapping("/chat")
    public String chatPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if(userDetails != null) {
            model.addAttribute("username", userDetails.getUsername());  // 로그인한 사용자 이름을 모델에 추가하여 HTML로 전달
        }

//        if (userDetails != null) {
//            System.out.println("Logged-in user: " + userDetails.getUsername());  // 사용자 이름 출력
//            model.addAttribute("username", userDetails.getUsername());
//        } else {
//            System.out.println("No user authenticated.");  // 인증되지 않은 경우
//        }

        return "index";  // templates/index.html을 렌더링
    }

    // "/app/chat.sendMessage"로 전송된 메시지를 처리
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")  // 모든 구독자에게 브로드캐스팅
    public ChatMessageDto sendMessage(ChatMessageDto chatMessage) {
        return chatMessage;  // 받은 메시지를 그대로 반환 (js-타임리프 문법으로 받아왔기 때문에 user 정보가 있음)
    }

    // 새로운 사용자가 입장할 때 호출
    @MessageMapping("/chat.newUser")
    @SendTo("/topic/public")
    public ChatMessageDto newUser(ChatMessageDto chatMessage) {
        chatMessage.setContent(chatMessage.getSender() + " joined the chat!");
        return chatMessage;
    }
}
