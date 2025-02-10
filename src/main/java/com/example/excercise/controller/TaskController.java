package com.example.excercise.controller;

import com.example.excercise.dto.TaskControllerDTO;
import com.example.excercise.entity.Project;
import com.example.excercise.entity.Task;
import com.example.excercise.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@CrossOrigin("*")
@Controller
@RequestMapping("/api/v1")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping(path = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Task>> getTasks() {
        return new ResponseEntity<>(taskService.getAllTasks(), HttpStatus.OK);
    }

    @PostMapping(path = "/projects/{projectId}/tasks", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createTask(@RequestBody TaskControllerDTO taskControllerDTO, String projectId) {
        return new ResponseEntity<>(taskService.createTask(taskControllerDTO, projectId), HttpStatus.OK);
    }

    @GetMapping(path = "/projects", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Project>> getProjects() {
        return new ResponseEntity<>(taskService.getProjects(), HttpStatus.OK);
    }
}
