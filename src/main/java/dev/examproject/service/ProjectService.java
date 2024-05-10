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

    public void addProject(Project project) {
        repository.addProject(project);
    }

    public Project getProject(int projectId) {
        return repository.getProject(projectId);
    }

    public List<Project> getProjectsForUser(int userId, String username) {
        return repository.getProjectsForUser(userId, username);
    }

    public List<Project> getSubProjectsForProject(int projectId) {
        return repository.getSubProjectsForProject(projectId);
    }

    public int getTotalRequiredHoursForAllSubProjects(int parentProjectId) {
        return repository.getTotalRequiredHoursForAllSubProjects(parentProjectId);
    }

    public Project getProjectById(int projectId) {
        return repository.getProjectById(projectId);
    }

    public boolean updateProject(Project project) {
        return repository.updateProject(project);
    }

}
