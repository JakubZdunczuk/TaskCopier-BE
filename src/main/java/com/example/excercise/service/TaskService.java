package com.example.excercise.service;

import com.example.excercise.dto.TaskControllerDTO;
import com.example.excercise.entity.Project;
import com.example.excercise.entity.Task;

import java.util.List;

public interface TaskService {

    List<Task> getAllTasks();
    List<Task> getTasks(String projectId);
    String createTask(TaskControllerDTO task, String projectId);
    List<Project> getProjects();
}
