package com.example.chat.repository;

import com.example.chat.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    // 각 유저 별 접근 가능한 채팅방 리스트 조회
    List<ChatRoomMember> findAllByMembers_Id(Long userId);

}
