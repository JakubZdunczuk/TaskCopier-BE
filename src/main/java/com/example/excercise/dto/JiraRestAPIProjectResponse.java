package com.example.excercise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JiraRestAPIProjectResponse {
    private String externalId;
    private String key;
    private String name;
}
