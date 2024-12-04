package com.example.chat.service;

import com.example.chat.dto.UserInviteDto;
import com.example.chat.entity.ChatRoom;
import com.example.chat.entity.ChatRoomMember;
import com.example.chat.entity.User;
import com.example.chat.repository.ChatRoomMemberRepository;
import com.example.chat.repository.ChatRoomRepository;
import com.example.chat.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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
    @Autowired
    private ChatRoomMemberRepository chatRoomMemberRepository;

    // 접근권한 있는 채팅방 리스트 조회
    public List<ChatRoom> getChatRoomForUser(Long userId){

        List<ChatRoomMember> chatRoomMemberList = chatRoomMemberRepository.findAllByMember_Id(userId);

        List<ChatRoom> chatRoomList = chatRoomMemberList.stream()
                .map(ChatRoomMember::getChatRoom) // ChatRoomMember -> ChatRoom 변환
                .distinct() // 중복 제거 (같은 ChatRoom에 여러 Member가 있을 수 있음)
                .collect(Collectors.toList()); // ChatRoom 리스트로 변환

        return chatRoomList;

    }

    // 채팅방 ID로 단일 채팅방 조회
    public Optional<ChatRoom> getChatRoomById(Long roomId) {

        return chatRoomRepository.findById(roomId);

    }

    // 초대가능한 멤버 조회 (신설 채팅방 : 본인 빼고 모두 조회)
    public List<UserInviteDto> getAvailableUsers(Long currentUserId) {

        List<User> allUsers = userRepository.findAll();
        return allUsers.stream()
                .filter(user -> !user.getId().equals(currentUserId)) // 본인을 제외
                .map(user -> new UserInviteDto(user.getId(), user.getName())) // User -> DTO로 매핑
                .collect(Collectors.toList());
    }

    // 초대가능한 멤버 조회 (이미 존재하는 채팅방 : 본인과 이미 초대된 사용자 빼고 모두 조회)
    public List<UserInviteDto> getAvailableUsers(Long currentUserId, Long chatRoomId) {

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
                .map(user -> new UserInviteDto(user.getId(), user.getName())) // User -> DTO로 매핑
                .collect(Collectors.toList());
    }

    // 채팅방 만들기
    @Transactional
    public ChatRoom createChatRoom(Long myUserId, List<UserInviteDto> inviteDtoList) {

        // 나 자신도 포함시켜서 기본이름 생성 및 초대
        User myUser = userRepository.findById(myUserId).orElseThrow();
        inviteDtoList.add(new UserInviteDto(myUser.getId(), myUser.getName()));
        
        // 채팅방 기본 이름 생성
        String defaultName = inviteDtoList.stream()
                .sorted(Comparator.comparing(UserInviteDto::getName))
                .map(UserInviteDto::getName)
                .collect(Collectors.joining(", "));

        // id 값만 리스트로 뽑기
        List<Long> userIds = inviteDtoList.stream()
                .map(UserInviteDto::getId)
                .collect(Collectors.toList());

        // 뽑은 id값으로 유저 객체 조회
        List<User> userList = userRepository.findAllById(userIds);

        // 1) 개인 채팅방의 경우, 이미 구성되어있는 채팅방이 있다면 채팅방을 새로 만들지 않고 기존에 구성된 채팅방을 반환
        if (userIds.size() == 2) {
            Optional<ChatRoom> existingChatRoom = chatRoomRepository.findPrivateChatRoomByUserIds(userIds.get(0), userIds.get(1));
            if (existingChatRoom.isPresent()) {
                return existingChatRoom.get(); // 기존 채팅방 반환
            }
        }

        // 2) 채팅방 만들기
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(defaultName);

        for (User user : userList) {

            ChatRoomMember chatRoomMember = new ChatRoomMember();
            chatRoomMember.setChatRoom(chatRoom);
            chatRoomMember.setMember(user);
            chatRoomMember.setCustomName(defaultName);

            chatRoom.getMembers().add(chatRoomMember);

        }

        return chatRoomRepository.save(chatRoom);

    }
}
