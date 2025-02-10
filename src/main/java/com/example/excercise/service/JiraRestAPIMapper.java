package com.example.excercise.service;

import com.example.excercise.dto.JiraRestAPIProjectResponse;
import com.example.excercise.dto.JiraRestAPITaskResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class JiraRestAPIMapper {

    public List<JiraRestAPITaskResponse> mapTasksFromJSON(ResponseEntity<JsonNode> responseEntity) {
        List<JiraRestAPITaskResponse> jiraRestApiTaskDTOList = new ArrayList<>();
        Objects.requireNonNull(responseEntity.getBody()).get("issues").forEach(issue ->
                jiraRestApiTaskDTOList.add(new JiraRestAPITaskResponse(
                        issue.get("id").toString(),
                        issue.get("fields").get("summary").toString(),
                        issue.get("fields").get("description").isNull() ? "" : issue.get("fields").get("description").get("content").get(0).get("content").get(0).get("text").toString(),
                        issue.get("fields").get("project").get("id").toString())
                )
        );
        return jiraRestApiTaskDTOList;
    }

    public List<JiraRestAPIProjectResponse> mapProjectsFromResponse(ResponseEntity<JsonNode> responseEntity) {
        List<JiraRestAPIProjectResponse> projects = new ArrayList<>();

        Objects.requireNonNull(responseEntity.getBody()).get("values").forEach(project ->
            projects.add(new JiraRestAPIProjectResponse(
                    project.get("id").toString(),
                    project.get("key").toString(),
                    project.get("name").toString()))
        );
        return projects;
    }
}
