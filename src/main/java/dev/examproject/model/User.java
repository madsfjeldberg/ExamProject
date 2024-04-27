package dev.examproject.model;

import lombok.Data;

import java.util.Objects;

@Data
public class User {

    private int userId;
    private String username;
    private String password;
    private String email;

    // No-args constructor so spring boot can create a new instance of this object
    public User() {}

    public User(String username, String password, String email) {
        this.userId = getUserIdAndIncrement();
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User(String username, String password) {
        this.userId = getUserIdAndIncrement();
        this.username = username;
        this.password = password;
    }

    public int getUserIdAndIncrement() {
        return userId++;
    }
}