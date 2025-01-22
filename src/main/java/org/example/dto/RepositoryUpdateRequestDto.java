package org.example.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RepositoryUpdateRequestDto {
    private String repositoryName;
    private String description;
    private String repositoryUrl;
}
