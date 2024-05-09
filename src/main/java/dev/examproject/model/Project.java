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

    public Project(int id, String name, String description, String admin) {
        this.projectId = id;
        this.name = name;
        this.description = description;
        this.admin = admin;
        this.assignedUsers = new ArrayList<>();
    }

    public Project(String projectName, String projectDescription) {
        this.name = projectName;
        this.description = projectDescription;
        this.assignedUsers = new ArrayList<>();
    }
    public Project(String projectName, String projectDescription, int parentProjectID) {
        this.name = projectName;
        this.description = projectDescription;
        this.parentProjectID = parentProjectID;
        this.assignedUsers = new ArrayList<>();
    }
    public Project(int projectId, String projectName, String projectDescription, int parentProjectID) {
        this.projectId = projectId;
        this.name = projectName;
        this.description = projectDescription;
        this.parentProjectID = parentProjectID;
        this.assignedUsers = new ArrayList<>();
    }
}
