package org.example.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.entity.ManagedRepo;

@Getter
@Setter
public class RepositoryResponseDto {
    private Long id;
    private String repositoryName;
    private String devStatus;
    private String devPeriod;
    private ManagedRepo.ProjectType projectType;

    public RepositoryResponseDto(Long id, String repositoryName,String devStatus, String devPeriod, ManagedRepo.ProjectType projectType) {
        this.id = id;
        this.repositoryName = repositoryName;
        this.devStatus = devStatus;
        this.devPeriod = devPeriod;
        this.projectType = projectType;
    }
}
