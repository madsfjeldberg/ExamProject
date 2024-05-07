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
    private List<String> assignedUsers;

    // No-args constructor so spring boot can create a new instance of this object
    public Project() {}

    public Project(String projectName, String projectDescription, String admin, int parentProjectID, List<String> assignedUsers) {
        this.name = projectName;
        this.description = projectDescription;
        this.admin = admin;
        this.parentProjectID = parentProjectID;
        this.assignedUsers = assignedUsers;
    }

    public Project(String projectName, String projectDescription, String admin, List<String> assignedUsers) {
        this.name = projectName;
        this.description = projectDescription;
        this.admin = admin;
        this.assignedUsers = assignedUsers;
    }

    public Project(String projectName, String projectDescription, String admin, int parentProjectID) {
        this.name = projectName;
        this.description = projectDescription;
        this.admin = admin;
        this.parentProjectID = parentProjectID;
        this.assignedUsers = new ArrayList<>();
    }

    public Project(String projectName, String projectDescription, String admin) {
        this.name = projectName;
        this.description = projectDescription;
        this.admin = admin;
        this.assignedUsers =new ArrayList<>();
    }

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
    public Project(String projectName, String projectDescription, int parentProjectID) {
        this.name = projectName;
        this.description = projectDescription;
        this.parentProjectID = parentProjectID;
        this.assignedUsers = new ArrayList<>();
    }

}
