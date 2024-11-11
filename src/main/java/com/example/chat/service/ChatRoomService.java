package com.example.chat.service;

import com.example.chat.entity.ChatRoom;
import com.example.chat.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    // 접근권한 있는 채팅방 리스트 조회
    public List<ChatRoom> getChatRoomForUser(Long userId){

        return chatRoomRepository.findAllByMembers_Id(userId);

    }

    public Optional<ChatRoom> getChatRoomById(Long roomId) {

        return chatRoomRepository.findById(roomId);

    }
}
