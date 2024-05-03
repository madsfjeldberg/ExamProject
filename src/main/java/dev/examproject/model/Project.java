package dev.examproject.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data public class Project {

    private int projectId;
    private String name;
    private String description;
    private String admin;
    private String parentProject;
    private List<String> assignedUsers;

    // No-args constructor so spring boot can create a new instance of this object
    public Project() {}

    public Project(String projectName, String projectDescription, String admin, String parentProject, List<String> assignedUsers) {
        this.projectId = getProjectIdAndIncrement();
        this.name = projectName;
        this.description = projectDescription;
        this.admin = admin;
        this.parentProject = parentProject;
        this.assignedUsers = assignedUsers;
    }

    public Project(String projectName, String projectDescription, String admin, List<String> assignedUsers) {
        this.projectId = getProjectIdAndIncrement();
        this.name = projectName;
        this.description = projectDescription;
        this.admin = admin;
        this.assignedUsers = assignedUsers;
    }

    public Project(String projectName, String projectDescription, String admin, String parentProject) {
        this.projectId = getProjectIdAndIncrement();
        this.name = projectName;
        this.description = projectDescription;
        this.admin = admin;
        this.parentProject = parentProject;
        this.assignedUsers = new ArrayList<>();
    }

    public Project(String projectName, String projectDescription, String admin) {
        this.projectId = getProjectIdAndIncrement();
        this.name = projectName;
        this.description = projectDescription;
        this.admin = admin;
        this.assignedUsers =new ArrayList<>();
    }

    public Project(String projectName, String projectDescription) {
        this.projectId = getProjectIdAndIncrement();
        this.name = projectName;
        this.description = projectDescription;
        this.assignedUsers = new ArrayList<>();
    }

    public int getProjectIdAndIncrement() {
        return projectId++;
    }
}
