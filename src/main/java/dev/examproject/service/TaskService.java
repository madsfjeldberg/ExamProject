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

    public void assignSelfToTask(int taskId, int userId) {
        repository.assignUserToTask(taskId, userId);
    }

    public void assignUserToTask(int taskId, int userId) {
        repository.assignUserToTask(taskId, userId);
    }

    public int getTotalRequiredHoursForProject(int projectId) {
        return repository.getTotalRequiredHoursForSubproject(projectId);
    }

    public void removeTaskUsersForProject(int projectId) {
        repository.removeTaskUsersForProject(projectId);
    }

    public void deleteTasksForProject(int projectId) {
        repository.deleteTasksForProject(projectId);
    }

    public void deleteTask(int taskId) {
        repository.deleteTask(taskId);
    }

    public void removeTaskUsers(int taskId) {
        repository.removeTaskUsers(taskId);
    }

    public Task getTask(int taskId) {
        return repository.getTask(taskId);
    }

    public int updateTask(Task task) {
        return repository.updateTask(task);
    }

}