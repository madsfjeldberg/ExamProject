package dev.examproject.repository;

import dev.examproject.model.Project;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProjectRepository {

    private List<Project> projects;

    public ProjectRepository() {
        this.projects = new ArrayList<>(List.of(
                new Project("Project 1", "Project 1 Description", "admin", new ArrayList<>()),
                new Project("Project 2", "Project 2 Description", "admin", List.of("user1", "user2")),
                new Project("Project 3", "Project 3 Description", "admin", new ArrayList<>())
        ));
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public void addProject(Project project) {
        projects.add(project);
    }

    public void deleteProject(String projectName) {
        projects.removeIf
                (project -> project.getName().equals(projectName));
    }

    public Project getProject(String projectName) {
        return projects.stream().filter
                (project -> project.getName().equals(projectName))
                .findFirst().orElse(null);
    }

    public List<Project> getAllProjects() {
        return projects;
    }

}
