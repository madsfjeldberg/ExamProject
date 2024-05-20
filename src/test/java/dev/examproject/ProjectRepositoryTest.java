package dev.examproject;

import dev.examproject.model.Project;
import dev.examproject.model.User;
import dev.examproject.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("h2")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // resetter databasen før hver test
public class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository repository;



    // Test that the repository adds a project
    // Not sure if it's supposed to be done like this?
    // basically only checks if it added *something*.
    @Test
    void addProject() {
        Project project = new Project("test", "test");
        int expected = 1;
        int actual = repository.addProject(project);
        assertEquals(expected, actual);
    }

    // Test that the repository returns the correct project id
    @Test
    void getId() {
        int expected = 1;
        int actual = repository.getId("test 1");
        assertEquals(expected, actual);
    }

    // Test that the repository returns the correct admin for a project
    @Test
    void getAdminForProject() {
        String expected = "test";
        String actual = repository.getAdminForProject(1);
        assertEquals(expected, actual);
    }

    // can't delete project before deleting project_users
    // i'll come back later
    /*
    @Test
    void deleteProject() {
        int expected = 1;
        int actual = repository.deleteProject(1);
        assertEquals(expected, actual);
    }
    */

    // Test that the repository returns the correct project
    @Test
    void getProject() {
        Project expected = repository.getProject(1);
        assertEquals(1, expected.getProjectId());
    }

    // skal skrives om senere, magter ikke lige nu
    @Test
    void getProjectsForUser() {
        List<Project> expected = new ArrayList<>(List.of(
                new Project(1, "test 1", "test 1", "test", 0, new ArrayList<>(List.of(new User("test", "test", "test@mail.dk"))), null),
                new Project(3, "test 3", "test 3", "test", 0, new ArrayList<>(List.of(new User("test", "test", "test@mail.dk"))), null)));
        List<Project> actual = repository.getProjectsForUser(1);
        assertEquals(expected, actual);
    }

    @Test
    void getAssignedUsers() {
        List<User> expected = new ArrayList<>(List.of(new User("test", "test", "test@mail.dk")));
        List<User> actual = repository.getAssignedUsers(1);
        assertEquals(expected, actual);
    }

    @Test
    void updateProject() {
        Project project = new Project(1, "test", "test", "test", 0, new ArrayList<>(List.of(new User("test", "test", "test@mail.dk"))), null);
        int expected = 1;
        int actual = repository.updateProject(project);
        assertEquals(expected, actual);
    }

    @Test
    void getTotalRequiredHoursForAllSubProjects() {
        int expected = 0;
        int actual = repository.getTotalRequiredHoursForAllSubProjects(1);
        assertEquals(expected, actual);
    }
}

