package dev.examproject.repository;

import dev.examproject.model.Project;
import dev.examproject.model.User;
import dev.examproject.repository.util.ConnectionManager;
import dev.examproject.repository.util.TurboLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProjectRepository {

    private static final TurboLogger log = new TurboLogger(ProjectRepository.class);

    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String dbUsername;
    @Value("${spring.datasource.password}")
    private String dbPassword;

    public ProjectRepository() {
    }

    public int addProject(Project project) {
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "INSERT INTO PROJECTS (name, description, parent_project_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());
            if (project.getParentProjectID() != 0) {
                ps.setInt(3, project.getParentProjectID());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int projectId = rs.getInt(1);
                    project.setProjectId(projectId); // Set the generated ID in the Task object
                    return 1;
                } else {
                    throw new SQLException("Failed to retrieve auto-generated key for project");
                }
            }
        } catch (SQLException e) {
            log.error("Error adding project", e);
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
            log.error("Error getting project id", e);
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
            log.error("Error getting project admin for projectId: " + project_id, e);
        }
        return null;
    }

    public int deleteProject(int projectId) {
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "DELETE FROM PROJECTS WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting project with ID: " + projectId, e);
        }
        return -1;
    }

    public void deleteSubProjects(int parentProjectId) {
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "DELETE FROM PROJECTS WHERE parent_project_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, parentProjectId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting sub-projects for project with ID: " + parentProjectId, e);
        }
    }

    // henter både subproject og project
    // /mads
    public Project getProject(int projectId) {
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "SELECT * FROM PROJECTS WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Project project = new Project(rs.getInt("id"), rs.getString("name"), rs.getString("description"));
                project.setAdmin(getAdminForProject(projectId));
                project.setAssignedUsers(getAssignedUsers(rs.getInt("id")));
                int parentProjectId = rs.getInt("parent_project_id");
                if (!rs.wasNull()) {
                    project.setParentProjectID(parentProjectId);
                }
                log.info("Project found: " + project.getName() + " with ID: " + project.getProjectId() + " and parent project ID: " + project.getParentProjectID());
                return project;
            }
        } catch (SQLException e) {
            log.error("Error getting project: " + projectId, e);
        }
        return null;
    }

    public List<Project> getProjectsForUser(int userId) {
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

                String adminUsername = getAdminForProject(project.getProjectId());
                project.setAdmin(adminUsername);

                projects.add(project);
            }
            return projects;
        } catch (SQLException e) {
            log.error("Error getting projects for userId: " + userId, e);
        }
        System.out.println("not returning anything");
        return null;
    }

    public List<User> getAssignedUsers(int projectId) {
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
            log.error("Error getting users for project", e);
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
            log.error("Error getting sub-projects for project with ID: " + projectId, e);
        }
        return null;
    }

    public int updateProject(Project project) {
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "UPDATE PROJECTS SET name = ?, description = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());
            ps.setInt(3, project.getProjectId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error updating project", e);
            return -1;
        }
    }

    public int getTotalRequiredHoursForAllSubProjects(int parentProjectId) {
        int totalHours = 0;
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "SELECT SUM(required_hours) FROM tasks WHERE project_id IN (SELECT id FROM projects WHERE parent_project_id = ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, parentProjectId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                totalHours = rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Error getting total required hours for all sub-projects", e);
        }
        return totalHours;
    }



}

