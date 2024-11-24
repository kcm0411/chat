package com.example.chat.controller;

import com.example.chat.dto.UserInviteDto;
import com.example.chat.entity.ChatRoom;
import com.example.chat.security.CustomUserDetails;
import com.example.chat.service.ChatRoomService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ChatApiController {

    @Autowired
    private ChatRoomService chatRoomService;

    @GetMapping("/api/chatrooms")
    public ResponseEntity<List<ChatRoom>> getChatList(@AuthenticationPrincipal CustomUserDetails userDetails) {

        List<ChatRoom> chatList = chatRoomService.getChatRoomForUser(userDetails.getId());
        return ResponseEntity.ok().body(chatList); // JSON 형식으로 반환

    }

    @GetMapping("/api/chatrooms/users/available")
    public ResponseEntity<List<UserInviteDto>> getInviteAvailableUserList(@AuthenticationPrincipal CustomUserDetails userDetails
                                                                        , @RequestParam(required = false) Long chatRoomId) {

        Long userId = userDetails.getId();

        List<UserInviteDto> userInviteDtos;

        if (chatRoomId != null) {
            userInviteDtos = chatRoomService.getAvailableUsers(userId, chatRoomId);
        } else {
            userInviteDtos = chatRoomService.getAvailableUsers(userId);
        }

        return ResponseEntity.ok() .body(userInviteDtos);

    }

    @PostMapping("/api/chatrooms")
    public ResponseEntity<ChatRoom> createChatRoom(@AuthenticationPrincipal CustomUserDetails userDetails
                                                            , @RequestBody List<UserInviteDto> inviteDtoList) throws BadRequestException {

        if (inviteDtoList == null || inviteDtoList.isEmpty()) {
            throw new BadRequestException("초대 할 유저를 선택해주세요.");
        }

        ChatRoom chatRoom = chatRoomService.createChatRoom(userDetails.getId(), inviteDtoList);

        return ResponseEntity.ok() .body(chatRoom);

    }

}
