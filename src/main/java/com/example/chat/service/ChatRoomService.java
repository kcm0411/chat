package com.example.chat.service;

import com.example.chat.entity.ChatRoom;
import com.example.chat.entity.ChatRoomMember;
import com.example.chat.entity.User;
import com.example.chat.repository.ChatRoomRepository;
import com.example.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private UserRepository userRepository;

    // 접근권한 있는 채팅방 리스트 조회
    public List<ChatRoom> getChatRoomForUser(Long userId){

        return chatRoomRepository.findAllByMembers_Id(userId);

    }

    // 채팅방 ID로 단일 채팅방 조회
    public Optional<ChatRoom> getChatRoomById(Long roomId) {

        return chatRoomRepository.findById(roomId);

    }

    // 초대가능한 멤버 조회 (신설 채팅방 : 본인 빼고 모두 조회)
    public List<User> getAvailableUsers(Long currentUserId) {

        List<User> allUsers = userRepository.findAll();
        return allUsers.stream()
                .filter(user -> !user.getId().equals(currentUserId)) // 본인을 제외
                .collect(Collectors.toList());
    }

    // 초대가능한 멤버 조회 (이미 존재하는 채팅방 : 본인과 이미 초대된 사용자 빼고 모두 조회)
    public List<User> getAvailableUsers(Long currentUserId, Long chatRoomId) {

        // 전체 유저 조회
        List<User> allUsers = userRepository.findAll();

        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow();

        // 이미 초대된 인원 조회
        Set<User> invitedUsers = chatRoom.getMembers().stream()
                .map(ChatRoomMember::getMember)
                .collect((Collectors.toSet()));

        return allUsers.stream()
                .filter(user -> !user.getId().equals(currentUserId)) // 본인을 제외
                .filter(user -> !invitedUsers.contains(user)) // 이미 초대된 사용자를 제외
                .collect(Collectors.toList());
    }

    // 채팅방 만들기
    public ChatRoom createChatRoom(String defaultName, List<Long> userIds) {

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(defaultName);

        for (Long memberId : userIds) {

            User user = userRepository.findById(memberId).orElseThrow();

            ChatRoomMember chatRoomMember = new ChatRoomMember();
            chatRoomMember.setChatRoom(chatRoom);
            chatRoomMember.setMember(user);

            chatRoom.getMembers().add(chatRoomMember);

        }

        return chatRoomRepository.save(chatRoom);
    }
}
