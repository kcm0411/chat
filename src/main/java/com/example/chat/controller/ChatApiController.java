package com.example.chat.controller;

import com.example.chat.dto.UserInviteDto;
import com.example.chat.entity.ChatRoom;
import com.example.chat.security.CustomUserDetails;
import com.example.chat.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class ChatApiController {

    @Autowired
    private ChatRoomService chatRoomService;

    @GetMapping("/api/chatlist")
    public ResponseEntity<List<ChatRoom>> getChatList(@AuthenticationPrincipal CustomUserDetails userDetails) {

        List<ChatRoom> chatList = chatRoomService.getChatRoomForUser(userDetails.getId());
        return ResponseEntity.ok().body(chatList); // JSON 형식으로 반환

    }

    @GetMapping("/api/users/available")
    public ResponseEntity<List<UserInviteDto>> getInviteAvailableUserList(@AuthenticationPrincipal CustomUserDetails userDetails
                                                                        , @RequestParam Optional<Long> chatRoomId) {

        Long userId = userDetails.getId();

        List<UserInviteDto> userInviteDtos;

        if (chatRoomId.isPresent()) {
            userInviteDtos = chatRoomService.getAvailableUsers(userId, chatRoomId.get());
        } else {
            userInviteDtos = chatRoomService.getAvailableUsers(userId);
        }

        return ResponseEntity.ok() .body(userInviteDtos);

    }


}
