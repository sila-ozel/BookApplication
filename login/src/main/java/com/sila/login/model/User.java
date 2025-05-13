package com.sila.login.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.*;

import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@SecondaryTable(
    name = "last_change_date",
    pkJoinColumns = @PrimaryKeyJoinColumn(name = "username")
)
public class User implements UserDetails {

    @Id
    @Column(name = "username", nullable = false, unique = true)
    @NonNull
    private String username;

    @Column(name = "password", nullable = false)
    @NonNull
    private String password;

    @Column(name = "role", nullable = false)
    @NonNull
    private String role;

    @Column(name = "no_of_pw")
    private int no_of_pw;

    @Column(name = "change_date", table = "last_change_date")
    private LocalDateTime change_date;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<LastPasswords> lastPasswords;

    // Constructors
    public User() {}

    public User(@NonNull String username, @NonNull String password, @NonNull String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(@NonNull String role) {
        this.role = role;
    }

    public int getNo_of_pw() {
        return no_of_pw;
    }

    public void setNo_of_pw(int no_of_pw) {
        this.no_of_pw = no_of_pw;
    }

    public LocalDateTime getChange_date() {
        return change_date;
    }

    public void setChange_date(LocalDateTime change_date) {
        this.change_date = change_date;
    }

    public List<LastPasswords> getLastPasswords() {
        return lastPasswords;
    }

    public void setLastPasswords(List<LastPasswords> lastPasswords) {
        this.lastPasswords = lastPasswords;
    }

    // Spring Security methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Avoid logging the password
    @Override
    public String toString() {
        return String.format("Username: %s, Role: %s", username, role);
    }
}
