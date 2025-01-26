package org.example.oauth;

import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="github_user") // table 이름 지정
public class GitHubUser {
    @Id
    @Column(name="user_id") // Column 이름 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) //반드시 있어야 하며, Unique 해야함.
    private String username;

    private String nickname; // Display Name(중복 가능함)

    @Column(nullable = true) // Email 비공개 User를 위해 NULL값도 허용
    private String email;

    private String profileImg;

    @CreationTimestamp // 자동으로 생성 시간 기록
    private Timestamp createDate;


    @Enumerated(EnumType.STRING) //Enum을 문자열로 저장
    @Column(nullable = false)
    private Role role; //권한 관리

    @Getter
    public enum Role{
        ROLE_USER("ROLE_USER"),
        ROLE_ADMIN("ROLE_ADMIN");

        private final String authority;

        Role(String authority) {
            this.authority = authority;
        }

    }
    // ADMIN 권한 확인 Method
    public boolean isAdmin() {
        return this.role == Role.ROLE_ADMIN;
    }

}
