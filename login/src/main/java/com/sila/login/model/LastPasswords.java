package com.sila.login.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Id;

@Entity
@Table(name="last_pw")
public class LastPasswords {
    @Id
    @Column(name="pw_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int pw_id;

    @ManyToOne(optional = false)
    @JoinColumn(name="username")
    private User user;

    @Column(name="pw_no")
    private int pw_no;

    @Column(name="pw")
    private String pw;

    public LastPasswords() {}

    public LastPasswords(User user, int pw_no, String pw) {
        this.user = user;
        this.pw_no = pw_no;
        this.pw = pw;
    }

    public String getPw() {
        return this.pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public int getPw_no() {
        return this.pw_no;
    }

    public void setPw_no(int pw_no) {
        this.pw_no = pw_no;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPw_id() {
        return this.pw_id;
    }

    public void setPw_id(int pw_id) {
        this.pw_id = pw_id;
    }

    @Override
    public String toString() {
        String str = String.format("Username: %s, pw_no: %d, pw_id: %d", user.getUsername(), pw_no, pw_id);
        return str;
    }
}
