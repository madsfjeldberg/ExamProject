package dev.examproject.repository;

import dev.examproject.model.Task;
import dev.examproject.repository.util.ConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TaskRepository {

    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String dbUsername;
    @Value("${spring.datasource.password}")
    private String dbPassword;

    public TaskRepository() {}

    public void addTask(Task task) {
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "INSERT INTO tasks (name, description, required_hours, project_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, task.getTaskName());
            ps.setString(2, task.getTaskDescription());
            ps.setInt(3, task.getRequiredHours());
            ps.setInt(4, task.getProjectId());
            ps.executeUpdate();

            // Retrieve the auto-generated ID
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int taskId = rs.getInt(1);
                    task.setTaskId(taskId); // Set the generated ID in the Task object
                } else {
                    // Handle the case where the auto-generated key couldn't be retrieved
                    throw new SQLException("Failed to retrieve auto-generated key for task");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Task> getProjectTasks(int projectId) {
        List<Task> tasks = new ArrayList<>();
        Connection conn = ConnectionManager.getConnection(dbUrl, dbUsername, dbPassword);
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
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    }
