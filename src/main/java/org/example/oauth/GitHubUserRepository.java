package org.example.oauth;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GitHubUserRepository extends JpaRepository<GitHubUser, Long> {
    GitHubUser findByNickname(String nickname);
}