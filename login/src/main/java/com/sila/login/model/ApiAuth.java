package com.sila.login.model;
// adding a comment
public class ApiAuth {
    private String username, password, age;

    public ApiAuth() {}

    public ApiAuth(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public int getAge() {
        return this.age;
    }
}
