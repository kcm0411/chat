package com.example.chat.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 ID

    private String name; // 채팅방 이름

    @ManyToMany
    private Set<User> members = new HashSet<>(); // User 와 ChatRoom 의 관계는 ManyToMany

    // 채팅방 초대기능
    public void addMember(User user) {
        members.add(user);
    }
}
