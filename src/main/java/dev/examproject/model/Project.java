package dev.examproject.model;

import lombok.Data;

@Data public class Project {

    private int projectId;
    private String projectName;
    private String projectDescription;

    // No-args constructor so spring boot can create a new instance of this object
    public Project() {}

    public Project(String projectName, String projectDescription) {
        this.projectId = getProjectIdAndIncrement();
        this.projectName = projectName;
        this.projectDescription = projectDescription;
    }

    public int getProjectIdAndIncrement() {
        return projectId++;
    }
}
