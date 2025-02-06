package com.example.excercise.service;

import com.example.excercise.repo.TaskRepository;
import com.example.excercise.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final RestAPITaskService restAPITaskService;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, RestAPITaskService restAPITaskService) {
        this.taskRepository = taskRepository;
        this.restAPITaskService = restAPITaskService;
    }

    @Override
    public List<Task> getTasks() {
        taskRepository.deleteAll();
        taskRepository.saveAll(restAPITaskService.getTasks());

        return taskRepository.findAll();
    }
}
