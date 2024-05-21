package dev.examproject.model;

import lombok.Data;

import java.util.List;

@Data public class Task {

    private int projectId;
    private int taskId;
    private String taskName;
    private String taskDescription;
    private int requiredHours;
    private List<User> assignedUsers;

    // No-args constructor so spring boot can create a new instance of this object
    public Task() {}

    // default constructor
    public Task(int projectId, String taskName, String taskDescription, int requiredHours) {
        this.projectId = projectId;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.requiredHours = requiredHours;
    }

    public Task(int projectId, int taskId, String taskName, String taskDescription, int requiredHours, List<User> assignedUsers) {
        this.projectId = projectId;
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.requiredHours = requiredHours;
        this.assignedUsers = assignedUsers;
    }
}
