package org.example.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.entity.ManagedRepo;

@Getter
@Setter
public class SingleRepositoryResponseDto {
    private Long id;
    private String repositoryName;
    private String repositoryUrl;
    private String description;
    private String devStatus;
    private String devPeriod;
    private ManagedRepo.ProjectType projectType;

    public SingleRepositoryResponseDto(Long id, String repositoryName, String repositoryUrl, String description, String devStatus, String devPeriod, ManagedRepo.ProjectType projectType) {
        this.id = id;
        this.repositoryName = repositoryName;
        this.repositoryUrl = repositoryUrl;
        this.description = description;
        this.devStatus = devStatus;
        this.devPeriod = devPeriod;
        this.projectType = projectType;
    }
}
