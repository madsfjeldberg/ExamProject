package dev.examproject.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data public class Project {

    private int projectId;
    private String name;
    private String description;
    private String admin;
    private int parentProjectID;
    private List<User> assignedUsers;
    private List<Task> tasks;

    // No-args constructor so spring boot can create a new instance of this object
    public Project() {}

    public Project(int projectId, String projectName, String projectDescription) {
        this.name = projectName;
        this.description = projectDescription;
        this.assignedUsers = new ArrayList<>();
        this.projectId = projectId;
    }

    public Project(String projectName, String projectDescription) {
        this.name = projectName;
        this.description = projectDescription;
        this.assignedUsers = new ArrayList<>();
    }
}
