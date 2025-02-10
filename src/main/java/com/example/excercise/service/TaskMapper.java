package com.example.excercise.service;

import com.example.excercise.dto.JiraRestAPIProjectResponse;
import com.example.excercise.dto.JiraRestAPITaskResponse;
import com.example.excercise.dto.TaskControllerDTO;
import com.example.excercise.entity.Project;
import com.example.excercise.entity.Task;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskMapper {

    public Task mapToTask(TaskControllerDTO taskControllerDTO, String projectId) {
        return null;
    }

    public Task mapToTask(JiraRestAPITaskResponse jiraRestAPITaskResponse) {
        return null;
    }

    public List<Task> mapToTaskList(List<JiraRestAPITaskResponse> jiraRestAPITaskResponseList) {
        return jiraRestAPITaskResponseList.stream().map(this::mapToTask).toList();
    }

    public List<Project> mapToProjectList(List<JiraRestAPIProjectResponse> jiraRestAPIProjectResponseList) {
        return null;
    }
}
