package dev.examproject;

import dev.examproject.model.Task;
import dev.examproject.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("h2")
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;
    @BeforeEach
    void addTestTask(){
        Task task = new Task(1, "user", "new user", 12);
        taskRepository.addTask(task);
    }
    @Test
    void addTask(){
        Task task = taskRepository.getTask(1);
       assertNotEquals(-1, task.getTaskId());
    }

    @Test
    void getProjectTasks(){
        List<Task> projectTask = taskRepository.getProjectTasks(1);
       assertNotEquals(0, projectTask.size());
    }

    @Test
    void getTotalRequiredHoursForSubproject(){
        int taskSize = taskRepository.getTotalRequiredHoursForSubproject(1);
        assertEquals(28, taskSize);
    }

    @Test
    void getAssignedUsers(){

    }


}
