package com.example.chat.controller;

import com.example.chat.entity.ChatRoom;
import com.example.chat.security.CustomUserDetails;
import com.example.chat.service.ChatRoomService;
import com.example.chat.dto.ChatMessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
public class ChatController {

    @Autowired
    private ChatRoomService chatRoomService;

    @GetMapping("/chat")
    public String chatListPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        if (userDetails == null) {
            return "login";
        }

        model.addAttribute("username", userDetails.getUsername());  // 로그인한 사용자 이름을 모델에 추가하여 HTML로 전달
        model.addAttribute("rooms", chatRoomService.getChatRoomForUser(userDetails.getId()));

        return "chatlist"; // 채팅방 리스트 화면으로 이동
    }

    @GetMapping("/chatlist")
    public ResponseEntity<List<ChatRoom>> getChatList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ChatRoom> chatList = chatRoomService.getChatRoomForUser(userDetails.getId());
        return ResponseEntity.ok().body(chatList); // JSON 형식으로 반환
    }


    @GetMapping("/chat/{roomId}")
    public String enterChatRoom(@PathVariable Long roomId, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        if (userDetails == null) {
            return "login";
        }

        model.addAttribute("username", userDetails.getUsername());
        Optional<ChatRoom> chatRoomOpt = chatRoomService.getChatRoomById(roomId); // 채팅방 객체 조회
        if (chatRoomOpt.isPresent()) {
            model.addAttribute("chatRoom", chatRoomOpt.get()); // .get 을 붙인 이유 : Optional 로 받은 객체이기때문에, 값이 있을때만 받아온다. (isPresent와 이중체크)
        } else {
            return "redirect:/chat"; // redirect 는 HTTP GET 요청으로 리다이렉트 한다. 만약 DeleteMapping("/chat") 이 있어도 그쪽으로 가지않음.
        }


        return "chatRoom";
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
