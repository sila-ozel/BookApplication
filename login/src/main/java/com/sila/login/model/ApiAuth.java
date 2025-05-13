package com.sila.login.model;

// Class to hold API login authentication data
public class ApiAuth {
    private String username;
    private String password;
    private int age; // corrected from String to int for consistency

    public ApiAuth() {
        // Default constructor
    }

    public ApiAuth(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
