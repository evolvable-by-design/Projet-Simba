package com.project.doodle.models;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

import static com.project.doodle.Utils.generateSlug;

@Entity
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String slug;
    private String title;
    private String location;
    private String description;
    private boolean has_meal;

    @CreationTimestamp
    private Date createdAt;

    @OneToMany(mappedBy = "poll")
    List<Choice> pollChoices;

    @ManyToMany
    @JoinTable(
            name = "poll_user",
            joinColumns = @JoinColumn(name = "poll_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    List<User> pollUsers;

    public Poll(){}

    public Poll(String title, String location, String description, boolean has_meal, List<Choice> pollChoices, List<User> pollUsers) {
        this.slug = id+generateSlug(24);
        this.title = title;
        this.location = location;
        this.description = description;
        this.has_meal = has_meal;
        this.pollChoices = pollChoices;
        this.pollUsers = pollUsers;
    }

    public void addUser(User user){
        this.pollUsers.add(user);
    }

    public void addChoice(Choice choice){
        this.pollChoices.add(choice);
    }

    public void removeUser(User user){
        this.pollUsers.remove(user);
    }

    public void removeChoice(Choice choice){
        this.pollChoices.remove(choice);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHas_meal() {
        return has_meal;
    }

    public void setHas_meal(boolean has_meal) {
        this.has_meal = has_meal;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<Choice> getPollChoices() {
        return pollChoices;
    }

    public void setPollChoices(List<Choice> pollChoices) {
        this.pollChoices = pollChoices;
    }

    public List<User> getPollUsers() {
        return pollUsers;
    }

    public void setPollUsers(List<User> pollUsers) {
        this.pollUsers = pollUsers;
    }


    @Override
    public String toString() {
        return "Poll{" +
                "id=" + id +
                ", slug='" + slug + '\'' +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", has_meal=" + has_meal +
                ", createdAt=" + createdAt +
                '}';
    }
}
