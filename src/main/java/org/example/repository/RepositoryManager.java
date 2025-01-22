package org.example.repository;
// DB와 상호작용하는 JPA Repository
import org.example.entity.ManagedRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositoryManager extends JpaRepository<ManagedRepository, Long> {
    List<ManagedRepository> findByUsername(String username);
}
