package com.project.doodle.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;
    private String first_name;
    private String last_name;
    private String email;

    @CreationTimestamp
    private Date createdAt;

    @JsonIgnore
    @ManyToMany(mappedBy = "users")
    List<Choice> userChoices;

    public User(){}

    public User(String username, String first_name, String last_name, String email, List<Choice> userChoices) {
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.userChoices = userChoices;
    }

    public void addChoice(Choice choice){
        this.userChoices.add(choice);
    }

    public void removeChoice(Choice choice){
        this.userChoices.remove(choice);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<Choice> getUserChoices() {
        return userChoices;
    }

    public void setUserChoices(List<Choice> userChoices) {
        this.userChoices = userChoices;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", email='" + email + '\'' +
                ", created_at=" + createdAt +
                '}';
    }
}
