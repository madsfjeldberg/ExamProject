package dev.examproject;

import dev.examproject.model.Task;
import dev.examproject.model.User;
import dev.examproject.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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
        Task task = new Task(1, "user", "new user", 12);
        int expected = 5;
        int actual = taskRepository.addTask(task);
        assertEquals(expected,actual);
    }

    @Test
    void getProjectTasks(){
        List<Task> projectTask = taskRepository.getProjectTasks(1);
        assertNotEquals(0, projectTask.size());
    }

    @Test
    void getTotalRequiredHoursForSubproject(){
        int taskSize = taskRepository.getTotalRequiredHoursForSubproject(1);
        assertEquals(taskSize, taskRepository.getTotalRequiredHoursForSubproject(1));
    }

    @Test
    void getAssignedUsers(){
        List<User> expected = new ArrayList<>();
        expected.add(new User("test", "test", "test@mail.dk"));
        List<User> actual = taskRepository.getAssignedUsers(1);
        assertEquals(expected, actual);
    }

    @Test
    void assignUserToTask(){
        int expected = 1;
        int actual = taskRepository.assignUserToTask(2,1);
        assertEquals(expected,actual);
    }

    @Test
    void deleteTask(){
        int expected = -1;
        int actual = taskRepository.deleteTask(2);
        assertEquals(expected, actual);
    }

    @Test
    void getTask(){
        Task expected = new Task(0, "test 1", "test 1", 1);
        expected.setTaskId(1);
        expected.setAssignedUsers(List.of(new User("test", "test", "test@mail.dk")));
        Task actual = taskRepository.getTask(1);
        assertEquals(expected, actual);
    }


    @Test
    void removeTaskUsers(){
        int expected = 1;
        int actual = taskRepository.removeTaskUsers(1);
        assertEquals(expected,actual);
    }

    @Test
    void removeTaskUsersForProject(){
        int expected = 1;
        int actual = taskRepository.removeTaskUsers(1);
        assertEquals(expected,actual);
    }

    @Test
    void deleteTasksForProject (){
        int expected = 1;
        int actual = taskRepository.deleteTasksForProject(1);
        assertEquals(expected,actual);
    }

    @Test
    void updateTask(){
        Task task= new Task(1, "test 1", "test 1", 1);
        int expected = -1;
        int actual = taskRepository.updateTask(task);
        assertEquals(expected, actual);
    }


}
