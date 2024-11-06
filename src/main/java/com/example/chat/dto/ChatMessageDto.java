package com.example.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

    private String content;  // 메시지 내용
    private String sender;   // 메시지를 보낸 사용자 이름
    private MessageType type;  // 메시지 유형
    private String time;

    // 메시지 타입을 정의하는 enum
    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
}
