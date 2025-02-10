package com.example.excercise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskControllerDTO {
    private String externalId;
    private String title;
    private String description;
    private String projectId;
    private String projectName;
}
