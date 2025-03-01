package org.example.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.entity.ManagedRepo;


@Getter
@Setter
public class RepositorySaveRequestDto {
    private String name;
    private String htmlUrl;
    private String description;
    private String createdAt;
    private String updatedAt;
    private String pushedAt;
    private ManagedRepo.ProjectType projectType;
}
