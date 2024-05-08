package dev.examproject.repository;

import dev.examproject.model.Project;
import dev.examproject.model.Task;
import dev.examproject.model.User;
import dev.examproject.repository.util.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProjectRepository {

    private static final Logger logger = LoggerFactory.getLogger(ProjectRepository.class);

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
                    return 1;
                } else {
                    // Handle the case where the auto-generated key couldn't be retrieved
                    throw new SQLException("Failed to retrieve auto-generated key for project");
                }
            }
        } catch (SQLException e) {
            logger.error("Error adding project", e);
        }
        return -1; // Return a default value indicating failure
    }
    public int addSubProject(Project project) {
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "INSERT INTO PROJECTS (name, description, parent_project_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());
            ps.setInt(3, project.getParentProjectID());
            ps.executeUpdate();

            // Retrieve the auto-generated project ID
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int projectId = rs.getInt(1);
                    project.setProjectId(projectId); // Set the generated ID in the Task object
                    return 1;
                } else {
                    // Handle the case where the auto-generated key couldn't be retrieved
                    throw new SQLException("Failed to retrieve auto-generated key for project");
                }
            }
        } catch (SQLException e) {
            logger.error("Error adding subproject", e);
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
            logger.error("Error getting project id", e);
        }
        return -1;
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
            logger.error("Error getting project admin for projectId: " + project_id, e);
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
                Project project = new Project(rs.getInt("id"), rs.getString("name"), rs.getString("description"));
                project.setAdmin(getAdminForProject(getId(name))); // det her er pis, find en løsning
                project.setAssignedUsers(getAssignedUsers(rs.getInt("id")));
                return project;
            }
        } catch (SQLException e) {
            logger.error("Error getting project: " + name, e);
        }
        return null;
    }

    public List<Project> getProjectsForUser(int userId, String username) {
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "SELECT PROJECTS.*, project_users.is_admin FROM PROJECTS " +
                "JOIN project_users ON PROJECTS.id = project_users.project_id " +
                "WHERE project_users.user_id = ? AND PROJECTS.parent_project_id IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            List<Project> projects = new ArrayList<>();
            while (rs.next()) {
                Project project = new Project(rs.getInt("id"), rs.getString("name"), rs.getString("description"));
                project.setAssignedUsers(getAssignedUsers(rs.getInt("id")));
                if (project.getAssignedUsers() == null) {
                    project.setAssignedUsers(new ArrayList<>());
                }
                if (rs.getBoolean("is_admin")) {
                    project.setAdmin(username);
                }
                projects.add(project);
            }
            System.out.println(projects);
            return projects;
        } catch (SQLException e) {
            logger.error("Error getting projects for user: " + username + "userId: " + userId, e);
        }
        System.out.println("not returning anything");
        return null;
    }

    private List<User> getAssignedUsers(int projectId) {
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "SELECT * FROM users WHERE id IN (SELECT user_id FROM project_users WHERE project_id = ?)";
        List<User> users = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(new User(rs.getString("username"), rs.getString("password"), rs.getString("email")));
            }
        } catch (SQLException e) {
            logger.error("Error getting users for project", e);
        }
        return users;
    }
    public List<Project> getSubProjectsForProject(int projectId) {
        List<Project> projects = new ArrayList<>();
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "SELECT * FROM projects WHERE parent_project_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Project project = new Project(rs.getInt("id"), rs.getString("name"), rs.getString("description"));
                project.setAdmin(getAdminForProject(projectId));
                project.setAssignedUsers(getAssignedUsers(rs.getInt("id")));
                project.setParentProjectID(rs.getInt("parent_project_id"));
                projects.add(project);
            }
            return projects;
        } catch (SQLException e) {
            logger.error("Error getting sub-projects for project with ID: " + projectId, e);
        }
        return null;
    }
    public Project getSubProject(String subProjectName) {
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "SELECT * FROM projects WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, subProjectName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Project project = new Project(rs.getInt("id"), rs.getString("name"), rs.getString("description"));
                project.setAdmin(getAdminForProject(rs.getInt("id")));
                project.setAssignedUsers(getAssignedUsers(rs.getInt("id")));
                project.setParentProjectID(rs.getInt("parent_project_id"));
                return project;
            }
        } catch (SQLException e) {
            logger.error("Error getting sub-project with name: " + subProjectName, e);
        }
        return null;
    }
}

