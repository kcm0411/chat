package com.example.chat.repository;

import com.example.chat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    Optional<User> findByUsername(String username);

    // 채팅방 초대 리스트 (전체인원 - 본인 - 이미초대된사람)
    @Query("SELECT u FROM User u WHERE u.id != :userId AND u NOT IN (SELECT m FROM ChatRoom r JOIN r.members m WHERE r.id = :chatRoomId)")
    List<User> findAvailableUsersForChatRoom(@Param("userId") Long userId, @Param("chatRoomId") Long chatRoomId);

}
