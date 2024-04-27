package dev.examproject.repository;

import dev.examproject.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {

    private List<User> users;


    public UserRepository() {
        this.users = new ArrayList<>(List.of(
                new User("admin", "admin"),
                new User("user", "user"),
                new User("user1", "user1")
        ));
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void deleteUser(String username) {
        users.removeIf
                (user -> user.getUsername().equals(username));
    }

    public User getUser(String username) {
        return users.stream().filter
                (user -> user.getUsername().equals(username))
                .findFirst().orElse(null);
    }

    public List<User> getAllUsers() {
        return users;
    }
}
