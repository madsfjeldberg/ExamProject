package dev.examproject.service;

import dev.examproject.model.Project;
import dev.examproject.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository repository;

    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    public void addProject(Project project, String username) {
        repository.addProject(project);
    }

    public int getProjectId(String projectName) {
        return repository.getId(projectName);
    }

    public Project getProject(String projectName) {
        return repository.getProject(projectName);
    }

    public List<Project> getProjectsForUser(int userId, String username) {
        return repository.getProjectsForUser(userId, username);
    }

}
