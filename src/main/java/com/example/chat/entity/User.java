package com.example.chat.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class) // @CreatedDate, @LastModifiedDate 사용하기위함
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본생성자 protected : 같은패키지 또는 상속구조 내에서만 객체 생성 가능
public class User {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 ID

    @Getter
    private String loginId;  // 로그인 ID
    private String password; // 비밀번호
    @Getter
    private String name;     // 사용자 이름

    @Enumerated(EnumType.STRING)
    @Getter
    private UserRole role;       // 사용자 권한

    @CreatedDate
    @Column(updatable = false) // 수정 시에는 변경되지 않도록 설정
    @Getter
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Getter
    private LocalDateTime updatedAt;

    // Password의 경우, 필요할 때만 접근하는 메서드 제공
    public String retrievePassword() {
        // 암호화된 형태 또는 가공된 데이터만 반환하도록 수정 가능
        return password;
    }

}
