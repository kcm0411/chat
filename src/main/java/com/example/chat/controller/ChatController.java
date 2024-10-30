package com.example.chat.controller;

import com.example.chat.dto.ChatMessageDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    // "/app/chat.sendMessage"로 전송된 메시지를 처리
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")  // 모든 구독자에게 브로드캐스팅
    public ChatMessageDto sendMessage(ChatMessageDto chatMessage) {
        return chatMessage;  // 받은 메시지를 그대로 반환 (구독자들에게 전달)
    }

    // 새로운 사용자가 입장할 때 호출
    @MessageMapping("/chat.newUser")
    @SendTo("/topic/public")
    public ChatMessageDto newUser(ChatMessageDto chatMessage) {
        chatMessage.setContent(chatMessage.getSender() + " joined the chat!");
        return chatMessage;
    }
}
