package org.example.oauth;

import jakarta.persistence.*;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;


@Getter
@Entity
@NoArgsConstructor
@Table(name="github_user") // table 이름 지정
public class GitHubUser {
    @Id
    @Column(name="user_id") // Column 이름 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String nickname;
    private String email;
    private String profileImg;
    private String role;

    @CreationTimestamp
    private Timestamp createDate;

    @Builder
    public GitHubUser(String email, String profileImg, String username, String nickname, String role) {
        this.email = email;
        this.profileImg = profileImg;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
        this.createDate = createDate;
    }

}
