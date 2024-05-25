package dev.examproject.model;

import lombok.Data;

@Data
public class User {

    private String username;
    private String password;
    private String email;

    // No-args constructor so spring boot can create a new instance of this object
    public User() {}

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

}
