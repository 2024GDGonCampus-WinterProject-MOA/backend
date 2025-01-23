package org.example.oauth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GitHubUserRepository extends JpaRepository<GitHubUser, Long> {
    //username으로 사용자 검색(고유 식별자)
    Optional<GitHubUser> findByUsername(String username);
}