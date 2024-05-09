package dev.examproject.service;

import dev.examproject.model.Task;
import dev.examproject.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public void addTask(Task task) {
        repository.addTask(task);
    }
    public List<Task> getProjectTasks(int projectId) {
        return repository.getProjectTasks(projectId);
    }

    public void addUserToTask(int taskId, int userId) {
        repository.addUserToTask(taskId, userId);
    }
}