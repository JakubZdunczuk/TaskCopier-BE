package com.example.excercise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JiraRestAPITaskResponse {
    private String externalId;
    private String title;
    private String description;
    private String projectId;
}
