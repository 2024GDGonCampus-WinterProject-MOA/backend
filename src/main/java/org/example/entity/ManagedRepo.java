package org.example.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name="repository")
public class ManagedRepo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String repository_name;
    private String repository_url;
    private String description;

    @Lob
    private String readme; // 해당 repository의 github에 올라와 있는 readme 파일

    @Lob
    private String moa_readme; // 해당 repository의 moa에서 생성한 가장 최근의 readme 파일 / .md 파일이 담김.
    private Integer has_moa_readme; // 해당 repository에 moa에서 생성한 readme 파일이 있는지 여부, 0:false / 1:true

    private LocalDateTime createdAt; // 첫 커밋 시간
    private LocalDateTime updatedAt; // 최근 업데이트 시간
    private LocalDateTime pushedAt; // 최근 푸쉬 시간


    // Getter, Setter

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getRepository_name() {
        return repository_name;
    }
    public void setRepository_name(String repository_name) {
        this.repository_name = repository_name;
    }

    public String getRepository_url() {
        return repository_url;
    }
    public void setRepository_url(String repository_url) {
        this.repository_url = repository_url;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getReadme() { return readme; }
    public void setReadme(String readme) { this.readme = readme; }

    public String getMoa_readme() { return moa_readme; }
    public void setMoa_readme(String moa_readme) { this.moa_readme = moa_readme; }

    public Integer getHas_moa_readme() { return has_moa_readme; }
    public void setHas_moa_readme(Integer has_moa_readme) { this.has_moa_readme = has_moa_readme; }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getPushedAt() {
        return pushedAt;
    }
    public void setPushedAt(LocalDateTime pushedAt) {
        this.pushedAt = pushedAt;
    }

}
