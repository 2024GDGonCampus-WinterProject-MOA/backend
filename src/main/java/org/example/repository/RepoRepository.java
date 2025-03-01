package org.example.repository;
// DB와 상호작용하는 JPA Repository
import org.example.entity.ManagedRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepoRepository extends JpaRepository<ManagedRepo, Long> {
    List<ManagedRepo> findByUsername(String username);
    // ProjectType 미선택인 레포지토리 조회를 위한 추가
    List<ManagedRepo> findByProjectType(ManagedRepo.ProjectType projectType);
}
