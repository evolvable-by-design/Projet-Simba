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

    private String title;
    private String location;
    private String description;
    private boolean has_meal;

    @CreationTimestamp
    private Date createdAt;

    @OneToMany(cascade = CascadeType.ALL)
    List<Choice> pollChoices;

    public Poll(){}

    public Poll(String title, String location, String description, boolean has_meal, List<Choice> pollChoices) {
        this.title = title;
        this.location = location;
        this.description = description;
        this.has_meal = has_meal;
        this.pollChoices = pollChoices;
    }

    public void addChoice(Choice choice){
        this.pollChoices.add(choice);
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

    @Override
    public String toString() {
        return "Poll{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", has_meal=" + has_meal +
                ", createdAt=" + createdAt +
                '}';
    }
}
