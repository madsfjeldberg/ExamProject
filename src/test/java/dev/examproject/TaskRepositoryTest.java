package dev.examproject;

import dev.examproject.model.Task;
import dev.examproject.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("h2")
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void addTask(){
       var task = new Task(1, "user", "new user", 12);
       var id = taskRepository.addTask(task);
       assertNotEquals(-1, id);
    }

    @Test
    void getProjectTasks(){
        var projectTask = taskRepository.getProjectTasks(1);
       assertNotEquals(0, projectTask);
    }


}
