package org.example.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.entity.ManagedRepo;

@Getter
@Setter
public class RepositoryUpdateRequestDto {
    private String repositoryName;
    private String description;
    private String repositoryUrl;
    private String devStatus;
    private ManagedRepo.ProjectType projectType;
}
