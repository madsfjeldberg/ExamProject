package dev.examproject.repository;

import dev.examproject.model.Task;
import dev.examproject.model.User;
import dev.examproject.repository.util.ConnectionManager;
import dev.examproject.repository.util.TurboLogger;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TaskRepository {

    private static final TurboLogger log = TurboLogger.getInstance(TaskRepository.class);

    public TaskRepository() {
    }

    public int addTask(Task task) {
        Connection conn = ConnectionManager.getConnection();
        String sql = "INSERT INTO tasks (name, description, required_hours, project_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, task.getTaskName());
            ps.setString(2, task.getTaskDescription());
            ps.setInt(3, task.getRequiredHours());
            ps.setInt(4, task.getProjectId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int taskId = rs.getInt(1);
                    task.setTaskId(taskId);// Set the generated ID in the Task object
                    return taskId;
                } else {
                    throw new SQLException("Failed to retrieve auto-generated key for task");
                }
            }
        } catch (SQLException e) {
            log.error("Error adding task", e);
        }
        return -1;
    }

    public List<Task> getProjectTasks(int projectId) {
        List<Task> tasks = new ArrayList<>();
        Connection conn = ConnectionManager.getConnection();
        String sql = "SELECT * FROM tasks WHERE project_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Task task = new Task();
                task.setTaskId(rs.getInt("id"));
                task.setTaskName(rs.getString("name"));
                task.setTaskDescription(rs.getString("description"));
                task.setRequiredHours(rs.getInt("required_hours"));
                task.setProjectId(rs.getInt("project_id"));
                task.setAssignedUsers(getAssignedUsers(task.getTaskId()));
                tasks.add(task);
            }
        } catch (SQLException e) {
            log.error("Error getting tasks for project with ID: " + projectId, e);
        }
        return tasks;
    }

    public int getTotalRequiredHoursForSubproject(int projectId) {
        Connection conn = ConnectionManager.getConnection();
        String sql = "SELECT SUM(required_hours) FROM tasks WHERE project_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Error getting total required hours for project", e);
        }
        return 0;
    }

    public List<User> getAssignedUsers(int taskId) {
        List<User> users = new ArrayList<>();
        Connection conn = ConnectionManager.getConnection();
        String sql = "SELECT * FROM users WHERE id IN (SELECT user_id FROM task_users WHERE task_id = ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                users.add(user);
            }
        } catch (SQLException e) {
            log.error("Error getting assigned users for task with ID: " + taskId, e);
        }
        return users;
    }

    public int assignUserToTask(int taskId, int userId) {
        Connection conn = ConnectionManager.getConnection();
        String sql = "INSERT INTO task_users (task_id, user_id) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ps.setInt(2, userId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error assigning user to task with ID: " + taskId, e);
        }
        return -1;
    }

    public Task getTask(int taskId) {
        Connection conn = ConnectionManager.getConnection();
        String sql = "SELECT * FROM tasks WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Task task = new Task();
                task.setTaskId(rs.getInt("id"));
                task.setTaskName(rs.getString("name"));
                task.setTaskDescription(rs.getString("description"));
                task.setRequiredHours(rs.getInt("required_hours"));
                task.setProjectId(rs.getInt("project_id"));
                task.setAssignedUsers(getAssignedUsers(task.getTaskId()));
                return task;
            }
        } catch (SQLException e) {
            log.error("Error getting task with ID: " + taskId, e);
        }
        return null;
    }

    public int removeTaskUsers(int taskId) {
        Connection conn = ConnectionManager.getConnection();
        String sql = "DELETE FROM task_users WHERE task_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error removing users from task with ID: " + taskId, e);
        }
        return -1;
    }

    public int deleteTask(int taskId) {
        Connection conn = ConnectionManager.getConnection();
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting task with ID: " + taskId, e);
        }
        return -1;
    }

    public void removeTaskUsersForProject(int projectId) {
        Connection conn = ConnectionManager.getConnection();
        String sql = """
                   DELETE FROM task_users
                   WHERE task_id IN (SELECT id FROM tasks WHERE project_id = ?) OR task_id IN (SELECT id FROM projects WHERE parent_project_id = ?)""";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            ps.setInt(2, projectId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error removing users from tasks for project with ID: " + projectId, e);
        }
    }

    public int deleteTasksForProject(int projectId) {
        Connection conn = ConnectionManager.getConnection();
        String sql = "DELETE FROM tasks WHERE project_id = ? OR project_id IN (SELECT id FROM projects WHERE parent_project_id = ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            ps.setInt(2, projectId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting tasks for project with ID: " + projectId, e);
        }
        return -1;
    }

    public int updateTask(Task task) {
        Connection conn = ConnectionManager.getConnection();
        String sql = "UPDATE tasks SET name = ?, description = ?, required_hours = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, task.getTaskName());
            ps.setString(2, task.getTaskDescription());
            ps.setInt(3, task.getRequiredHours());
            ps.setInt(4, task.getTaskId());
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0 ? 1 : -1;
        } catch (SQLException e) {
            log.error("Error updating task", e);
            return -1;
        }

    }
}
