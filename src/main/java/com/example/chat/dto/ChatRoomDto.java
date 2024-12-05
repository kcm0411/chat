package com.example.chat.dto;

import com.example.chat.entity.ChatRoom;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomDto {
    private Long id;
    private String name;
    private int memberCount;

    public ChatRoomDto(ChatRoom chatRoom) {
        this.id = chatRoom.getId();
        this.name = chatRoom.getName();
        this.memberCount = chatRoom.getMembers().size(); // members의 크기로 계산
    }

}
