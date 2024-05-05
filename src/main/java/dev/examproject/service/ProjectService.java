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

    public void deleteProject(String projectName) {
        repository.deleteProject(projectName);
    }

    public Project getProject(String projectName) {
        return repository.getProject(projectName);
    }

    public void setProjects(List<Project> projects) {
        repository.setProjects(projects);
    }

    public List<Project> getAllProjects() {
        return repository.getAllProjects();
    }

    public List<Project> getProjectsForUser(String username) {
        return repository.getAllProjects().stream()
                .filter(project -> project.getAssignedUsers().contains(username) || project.getAdmin().equals(username))
                .toList();
    }

}
