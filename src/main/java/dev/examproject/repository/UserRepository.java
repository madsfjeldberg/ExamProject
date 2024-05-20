package dev.examproject.repository;

import dev.examproject.model.User;
import dev.examproject.repository.util.ConnectionManager;
import dev.examproject.repository.util.TurboLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserRepository {

    private static final TurboLogger log = new TurboLogger(UserRepository.class);

    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String dbUsername;
    @Value("${spring.datasource.password}")
    private String dbPassword;

    public UserRepository() {}

    public User authenticateUser(String username, String password) {
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "SELECT * FROM USERS WHERE username = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("username"), rs.getString("password"), rs.getString("email"));
            }
        } catch (SQLException e) {
            log.error("Error while authenticating user", e);
        }
        return null;
    }

    public int getUserId(String username) {
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "SELECT id FROM USERS WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            log.error("Error while getting user id", e);
        }
        return -1;
    }

    public int addUser(User user) {
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "INSERT INTO USERS (username, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            return ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error while adding user", e);
        }
        return -1;
    }

    public int addUserToProject( User user, int projectId) {
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "INSERT INTO project_users (user_id, project_id, is_admin) VALUES (?, ?, false)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, getUserId(user.getUsername()));
            ps.setInt(2, projectId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error while adding user to project:", e);
        }
        return -1;
    }

    public void removeUsersFromProject(int projectId) {
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = """
                DELETE FROM project_users
                WHERE project_id = ?
                OR project_id IN (SELECT id FROM projects WHERE parent_project_id = ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            ps.setInt(2, projectId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error while removing users from project", e);
        }
    }

    public int setUserToAdmin(String username, int projectId) {
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "UPDATE project_users SET is_admin = true WHERE user_id = ? AND project_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, getUserId(username));
            ps.setInt(2, projectId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error while setting user to admin", e);
        }
        return -1;
    }

    public String getUsername(int userId) {
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "SELECT username FROM USERS WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            log.error("Error while getting username", e);
        }
        return null;
    }

    public User getUser(String username) {
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "SELECT * FROM USERS WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("username"), rs.getString("password"), rs.getString("email"));
            }
        } catch (SQLException e) {
            log.error("Error while getting user", e);
        }
        return null;
    }

}
