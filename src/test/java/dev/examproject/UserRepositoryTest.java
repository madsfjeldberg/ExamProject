package dev.examproject;

import dev.examproject.model.User;
import dev.examproject.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("h2")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // resetter databasen før hver test
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Test
    void getUser() {
        // Test that the repository returns the correct user
        User expected = repository.getUser("test");
        assertEquals("test", expected.getUsername());
    }

    @Test
    void authenticateUser() {
        // Test that the repository authenticates the user
        User expected = repository.authenticateUser("test", "test");
        assertEquals("test", expected.getUsername());
    }

    @Test
    void getUserId() {
        // Test that the repository returns the correct user id
        int expected = repository.getUserId("test");
        assertEquals(1, expected);
    }

    @Test
    void addUser() {
        // Test that the repository adds a user
        User user = new User("test2", "test2", "test2@mail.dk");
        int expected = 1;
        int actual = repository.addUser(user);
        assertEquals(expected, actual);
    }

    @Test
    void getUsername() {
        // Test that the repository returns the correct username
        String expected = "test";
        String actual = repository.getUsername(1);
        assertEquals(expected, actual);
    }

    @Test
    void addUserToProject() {
        // Test that the repository adds the user to the project
        // "test2" user is not a member of project 1
        int expected = 1;
        int actual = repository.addUserToProject(repository.getUser("test2"), 1);
        assertEquals(expected, actual);
    }

    @Test
    void setUserToAdmin() {
        // Test that the repository sets the user to admin
        // "test2" user is not an admin for project 2
        int expected = 1;
        int actual = repository.setUserToAdmin("test2", 2);
        assertEquals(expected, actual);
    }

}
