package com.example.chat.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "chat_room_member")
public class ChatRoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    @Setter
    @Getter
    @JsonBackReference // ChatRoomMember -> ChatRoom 직렬화 제외
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "member_id")
    @Getter
    @Setter
    private User member;

    @Setter
    private String customName; // 사용자별 커스텀 채팅방 이름

}
