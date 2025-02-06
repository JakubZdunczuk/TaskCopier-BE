package com.example.excercise.service;

import com.example.excercise.entity.Task;

import java.util.List;

public interface RestAPITaskService {

    List<Task> getTasks();
    String copyTask(Task task);
}
