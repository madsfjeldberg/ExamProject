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

    public void deleteProjekt(int projectID){
        repository.deleteProject(projectID);
    }

    public List<Project> getProjectsForUser(int userId) {
        return repository.getProjectsForUser(userId);

    }

    public List<Project> getSubProjectsForProject(int projectId) {
        return repository.getSubProjectsForProject(projectId);
    }

    public void deleteProject(int projectId) {
        repository.deleteProject(projectId);
    }

    public void deleteSubProjects(int projectId) {
        repository.deleteSubProjects(projectId);
    }

    public int getTotalRequiredHoursForAllSubProjects(int parentProjectId) {
        return repository.getTotalRequiredHoursForAllSubProjects(parentProjectId);
    }

    /*
    public Project getProjectById(int projectId) {
        return repository.getProjectById(projectId);
    }

     */

    public int updateProject(Project project) {
        return repository.updateProject(project);
    }

}
