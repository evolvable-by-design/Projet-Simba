package com.project.doodle.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Choice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    private Date start_date;

    @Temporal(TemporalType.TIMESTAMP)
    private Date end_date;

    @ManyToMany
    @JoinTable(
            name = "choice_user",
            joinColumns = @JoinColumn(name = "choice_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users;


    public Choice(){}

    public Choice(String name, Date start_date, Date end_date, List<User> users) {
        this.name = name;
        this.start_date = start_date;
        this.end_date = end_date;
        this.users = users;
    }

    public void addUser(User user){
        users.add(user);
    }

    public void removeUser(User user){
        users.remove(user);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Choice{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                '}';
    }
}
