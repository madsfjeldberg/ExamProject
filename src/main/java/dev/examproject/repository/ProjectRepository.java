package dev.examproject.repository;

import dev.examproject.model.Project;
import dev.examproject.repository.util.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());
            return ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error adding project", e);
        }
        return -1;
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
                Project project = new Project(rs.getString("name"), rs.getString("description"));
                project.setAdmin(getAdminForProject(getId(name)));
                return project;
            }
        } catch (SQLException e) {
            logger.error("Error getting project: " + name, e);
        }
        return null;
    }

    // TODO: der er noget der ikke virker her.
    // TODO: hvis man ikke er admin, sætter den en bruger til at være admin alligevel?
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
            logger.error("Error getting projects for user: " + username + "userId: " + userId, e);
        }
        return null;
    }
}
