package com.example.chat.repository;

import com.example.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 개인채팅방 존재유무 체크
    @Query(value = "SELECT DISTINCT cr.* " +
            "FROM chat_room cr " +
            "JOIN chat_room_member m1 ON cr.id = m1.chat_room_id " +
            "JOIN chat_room_member m2 ON cr.id = m2.chat_room_id " +
            "WHERE m1.members_id = :userId1 AND m2.members_id = :userId2 " +
            "AND (SELECT COUNT(*) FROM chat_room_member WHERE chat_room_id = cr.id) = 2",
            nativeQuery = true)
    Optional<ChatRoom> findPrivateChatRoomByUserIds(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

}
