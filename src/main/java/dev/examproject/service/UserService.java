package dev.examproject.service;

import dev.examproject.model.User;
import dev.examproject.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public void addUser(User user) {
        repository.addUser(user);
    }

    public void setUserToAdmin(String username, int projectId) {
        repository.setUserToAdmin(username, projectId);
    }

    public int getUserId(String username) {
        return repository.getUserId(username);
    }

    public User getUser(String name) {
        return repository.getUser(name);
    }

    public User authenticateUser(String username, String password) {
        return repository.authenticateUser(username, password);
    }

    public int addUserToProject(User user, int projectId) {
        return repository.addUserToProject(user, projectId);
    }
}
