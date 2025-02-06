package com.example.excercise.controller;

import com.example.excercise.entity.Task;
import com.example.excercise.service.RestAPITaskService;
import com.example.excercise.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@CrossOrigin("*")
@Controller
@RequestMapping("/api/task")
public class TaskController {

    private final TaskService taskService;
    private final RestAPITaskService restApiTaskService;

    public TaskController(TaskService taskService, RestAPITaskService restApiTaskService) {
        this.taskService = taskService;
        this.restApiTaskService = restApiTaskService;
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Task>> getTasks() {


        return new ResponseEntity<>(taskService.getTasks(), HttpStatus.OK);
    }

    @PostMapping(path = "/copy", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> copy(@RequestBody Task task) {

        return new ResponseEntity<>(restApiTaskService.copyTask(task), HttpStatus.OK);
    }
}
