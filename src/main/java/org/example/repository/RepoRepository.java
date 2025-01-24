package org.example.repository;
// DB와 상호작용하는 JPA Repository
import org.example.entity.ManagedRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepoRepository extends JpaRepository<ManagedRepo, Long> {
    List<ManagedRepo> findByUsername(String username);
}
