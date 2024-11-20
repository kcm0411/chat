package com.example.chat.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
public class ChatRoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "members_id")
    @Getter
    private User member;

    private String customName; // 사용자별 커스텀 채팅방 이름

}