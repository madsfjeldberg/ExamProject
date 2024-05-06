package dev.examproject;

import dev.examproject.model.User;
import dev.examproject.repository.UserRepository;
import dev.examproject.repository.util.ConnectionManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("h2")
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
        System.out.println(repository.addUser(user));
        assertEquals(user, repository.getUser("test2"));
    }

    // Helper method so as not to use "getUser" from repo.

}
