package com.example.excercise.service;

import com.example.excercise.dto.JiraRestAPIProjectResponse;
import com.example.excercise.dto.JiraRestAPITaskResponse;
import com.example.excercise.dto.TaskControllerDTO;
import com.example.excercise.entity.Project;
import com.example.excercise.repo.TaskRepository;
import com.example.excercise.entity.Task;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final JiraRestApiService jiraRestApiService;
    private final TaskMapper taskMapper;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, JiraRestApiService jiraRestApiService, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.jiraRestApiService = jiraRestApiService;
        this.taskMapper = taskMapper;
    }

    @Override
    public List<Task> getAllTasks() {
        taskRepository.deleteAll();
        List<JiraRestAPITaskResponse> taskRequests = jiraRestApiService.getAllTasks();
        taskRepository.saveAll(taskMapper.mapToTaskList(taskRequests));

        return taskRepository.findAll();
    }

    @Override
    public List<Task> getTasks(String projectId) {
        return List.of();
    }

    @Override
    public String createTask(TaskControllerDTO taskControllerDTO, String projectId) {
        Task task = taskMapper.mapToTask(taskControllerDTO, projectId);
        ResponseEntity<JsonNode> response = jiraRestApiService.createTask(task);
        if (response.getStatusCode().is2xxSuccessful()) {
            task.setExternalId(Objects.requireNonNull(response.getBody()).get("id").toString());
            taskRepository.save(task);
            return response.toString();
        }
        return response.toString();
    }


    @Override
    public List<Project> getProjects() {
        List<JiraRestAPIProjectResponse> jiraRestAPIProjectResponseList = jiraRestApiService.getProjects();
        return taskMapper.mapToProjectList(jiraRestAPIProjectResponseList);
    }
}
