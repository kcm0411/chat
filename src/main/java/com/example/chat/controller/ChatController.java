package com.example.chat.controller;

import com.example.chat.dto.ChatMessageDto;
import com.example.chat.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

//    // "/app/chat.sendMessage"로 전송된 메시지를 처리
//    @MessageMapping("/chat.sendMessage")
//    @SendTo("/topic/public")  // 모든 구독자에게 브로드캐스팅
//    public ChatMessageDto sendMessage(ChatMessageDto chatMessage) {
//        return chatMessage;  // 받은 메시지를 그대로 반환 (js-타임리프 문법으로 받아왔기 때문에 user 정보가 있음)
//    }

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public ChatController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    // 모든 유저가 아닌, 특정 채팅방에 접근권한이 있는 유저에게만 브로드캐스팅
    @MessageMapping("/chat/{roomId}.sendMessage")
    public void sendMessage(@DestinationVariable String roomId, ChatMessageDto chatMessage) {
        System.out.println("Message received: " + chatMessage.getContent() + " for room: " + roomId);
        simpMessagingTemplate.convertAndSend("/topic/chatroom/" + roomId, chatMessage);
    }





//    // 새로운 사용자가 입장할 때 호출
//    @MessageMapping("/chat.newUser")
//    @SendTo("/topic/public")
//    public ChatMessageDto newUser(ChatMessageDto chatMessage) {
//        chatMessage.setContent(chatMessage.getSender() + " joined the chat!");
//        return chatMessage;
//    }

    // 새로운 사용자가 입장 시 알림 메시지 전송
    @MessageMapping("/chat/{roomId}/newUser")
    @SendTo("/topic/chatroom/{roomId}")
    public void newUser(@DestinationVariable String roomId, ChatMessageDto chatMessage) {
        chatMessage.setContent(chatMessage.getSender() + " joined the chat!");
        simpMessagingTemplate.convertAndSend("/topic/chatroom/" + roomId, chatMessage);
    }
}
