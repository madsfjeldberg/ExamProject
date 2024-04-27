package dev.examproject.model;

import lombok.Data;

@Data public class Task {

    private int projectId;
    private int taskId;
    private String taskName;
    private String taskDescription;
    private int parentTaskId;
    private int requiredHours;

    // No-args constructor so spring boot can create a new instance of this object
    public Task() {}

    // default constructor
    public Task(int projectId, String taskName, String taskDescription, int parentTaskId, int requiredHours) {
        this.projectId = projectId;
        this.taskId = getTaskIdAndIncrement();
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.parentTaskId = parentTaskId;
        this.requiredHours = requiredHours;
    }

    // overloading for creating basic tasks
    // can set hours, parent task at a later time
    public Task(int projectId, String taskName, String taskDescription) {
        this.projectId = projectId;
        this.taskId = getTaskIdAndIncrement();
        this.taskName = taskName;
        this.taskDescription = taskDescription;
    }

    private int getTaskIdAndIncrement() {
        return taskId++;
    }

}
