package com.magicliang.experiments.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by magicliang on 2016/4/23.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class User implements Serializable {

    private static final long serialVersionUID = 1l;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    private String password;

    private Boolean isLocked;

    private Boolean isExpired;

    private Boolean isEnabled;

    private String email;

    private String role;

    public User() {
        super();
    }

    public User(String name) {
        this(name, "123");
    }

    public User(String name, String password) {
        this(name, password, null, null, null, null, null);
    }

    public User(String name, String password, Boolean isLocked, Boolean isExpired, Boolean isEnabled, String email, String role) {
        this();
        this.name = name;
        this.password = password;
        this.isLocked = isLocked;
        this.isExpired = isExpired;
        this.isEnabled = isEnabled;
        this.email = email;
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isLocked=" + isLocked +
                ", isExpired=" + isExpired +
                ", isEnabled=" + isEnabled +
                ", email='" + email + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
    //Don't allow set id

    public void setName(String name) {
        this.name = name;
    }

    public Boolean IsLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getExpired() {
        return isExpired;
    }

    public void IsExpired(Boolean expired) {
        isExpired = expired;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean IsEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
