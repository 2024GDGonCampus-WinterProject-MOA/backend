package org.example.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RepositoryResponseDto {
    private Long id;
    private String repositoryName;
    private String repositoryUrl;
    private String description;

    public RepositoryResponseDto(Long id, String repositoryName, String repositoryUrl, String description) {
        this.id = id;
        this.repositoryName = repositoryName;
        this.repositoryUrl = repositoryUrl;
        this.description = description;
    }
}
