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

    // Add other methods as needed, for example:
    // public Task getTask(int id) { ... }
    // public List<Task> getAllTasks() { ... }
    // public void updateTask(Task task) { ... }
    // public void deleteTask(int id) { ... }
}