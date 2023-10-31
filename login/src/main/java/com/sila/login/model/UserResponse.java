package com.sila.login.model;

public class UserResponse {
    private String username, role;
    private long time_diff;
    private boolean should_change;

    public UserResponse() {

    }

    public UserResponse(String username, String role, long time_diff,boolean should_change) {
        this.username = username;
        this.role = role;
        this.time_diff = time_diff;
        this.should_change = should_change;
    }

    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getRole() {
        return this.role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public long getTime_diff() {
        return this.time_diff;
    }
    public void setTime_diff(long time_diff) {
        this.time_diff = time_diff;
    }
    public boolean getShould_change() {
        return this.should_change;
    }
    public void setShould_change(boolean should_change) {
        this.should_change = should_change;
    }
}
