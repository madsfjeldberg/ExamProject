package dev.examproject;

import dev.examproject.model.Project;
import dev.examproject.repository.ProjectRepository;
import dev.examproject.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("h2")
public class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository repository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void addProject() {
        // Test that the repository adds a project
        // Not sure if it's supposed to be done like this?
        // basically only checks if it added *something*.
        Project project = new Project("test", "test");
        int expected = 1;
        int actual = repository.addProject(project);
        assertEquals(expected, actual);
    }

    @Test
    void getId() {
        // Test that the repository returns the correct project id
        int expected = 1;
        int actual = repository.getId("test 1");
        assertEquals(expected, actual);
    }

    @Test
    void getAdminForProject() {
        // Test that the repository returns the correct admin for a project
        String expected = "test";
        String actual = repository.getAdminForProject(1);
        assertEquals(expected, actual);
    }

    @Test
    void getProject() {
        // Test that the repository returns the correct project
        Project expected = repository.getProject("test 1");
        assertEquals("test 1", expected.getName());
    }

    // skal skrives om senere, magter ikke lige nu
    /*
    @Test
    void getProjectsForUser() {
        // Test that the repository returns the correct projects for a user
        List<Project> expected = new ArrayList<>(List.of(
                new Project(1, "test 1", "test 1", "test"),
                new Project(3, "test 3", "test 3", "test")));
        List<Project> actual = repository.getProjectsForUser(1, "test");
        assertEquals(expected, actual);
    }

     */

        @Test
        void testUpdateProject() {
            Project project = new Project(1, "Updated Project", "Updated Description");
            when(projectRepository.updateProject(project)).thenReturn(true);

            boolean result = projectService.updateProject(project);

            assertTrue(result, "Project should be updated successfully");
            verify(projectRepository).updateProject(project);
        }

        @Test
        void testUpdateProjectFailure() {
            Project project = new Project(1, "Failed Update", "Failed Description");
            when(projectRepository.updateProject(project)).thenReturn(false);

            boolean result = projectService.updateProject(project);

            assertFalse(result, "Project should fail to update");
            verify(projectRepository).updateProject(project);
        }
    }

