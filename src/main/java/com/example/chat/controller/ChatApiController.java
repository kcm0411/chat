package com.example.chat.controller;

import com.example.chat.entity.ChatRoom;
import com.example.chat.security.CustomUserDetails;
import com.example.chat.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChatApiController {

    @Autowired
    private ChatRoomService chatRoomService;

    @GetMapping("/chatlist")
    public ResponseEntity<List<ChatRoom>> getChatList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ChatRoom> chatList = chatRoomService.getChatRoomForUser(userDetails.getId());
        return ResponseEntity.ok().body(chatList); // JSON 형식으로 반환
    }

}
