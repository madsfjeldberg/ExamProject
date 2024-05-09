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
    public void addSubProject(Project project) {
        repository.addSubProject(project);
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
    public List<Project> getSubProjectsForProject(int projectId) {
        return repository.getSubProjectsForProject(projectId);
    }
    public Project getSubProject(String subProjectName) {
        return repository.getSubProject(subProjectName);
    }

    //--------------------------------------------EDIT---------------------------------------

    public Project getProjectById(int projectId) {
        return repository.getProjectById(projectId);
    }

    public boolean updateProject(Project project) {
        return repository.updateProject(project);
    }

}
