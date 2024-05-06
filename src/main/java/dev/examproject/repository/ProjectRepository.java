package dev.examproject.repository;

import dev.examproject.model.Project;
import dev.examproject.repository.util.ConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProjectRepository {

    private List<Project> projects;

    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String dbUsername;
    @Value("${spring.datasource.password}")
    private String dbPassword;

    public ProjectRepository() {}

    public int addProject(Project project) {
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "INSERT INTO PROJECTS (name, description) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());
            ps.executeUpdate();

            // Retrieve the auto-generated project ID
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int projectId = rs.getInt(1);
                    project.setProjectId(projectId); // Set the generated ID in the Task object
                } else {
                    // Handle the case where the auto-generated key couldn't be retrieved
                    throw new SQLException("Failed to retrieve auto-generated key for project");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return a default value indicating failure
    }


    public int getId(String projectName) {
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "SELECT id FROM PROJECTS WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, projectName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void deleteProject(String projectName) {
        projects.removeIf
                (project -> project.getName().equals(projectName));
    }

    public String getAdminForProject(int project_id) {
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "SELECT users.username FROM users " +
                "JOIN project_users ON users.id = project_users.user_id " +
                "WHERE project_users.project_id = ? AND project_users.is_admin = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, project_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Project getProject(String name) {
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "SELECT PROJECTS.*, project_users.is_admin FROM PROJECTS " +
                "JOIN project_users ON PROJECTS.id = project_users.project_id " +
                "WHERE PROJECTS.name = ? ";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Project project = new Project(rs.getString("name"), rs.getString("description"));
                project.setAdmin(getAdminForProject(getId(name)));
                return project;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Project> getProjectsForUser(int userId, String username) {
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        // TODO: skriv om så den også henter projekter hvor man er medlem/assigned
        String sql = "SELECT PROJECTS.*, project_users.is_admin FROM PROJECTS " +
                "JOIN project_users ON PROJECTS.id = project_users.project_id " +
                "WHERE project_users.user_id = ? ";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            List<Project> projects = new ArrayList<>();
            while (rs.next()) {
                Project project = new Project(rs.getString("name"), rs.getString("description"));
                project.setAdmin(username);
                projects.add(project);
            }
            return projects;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
