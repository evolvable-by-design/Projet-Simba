package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import utils.Utils;

@Entity

public class Poll extends PanacheEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
	public Long id;  
    
    @Column(name = "slug", updatable = false, nullable = false)
    public String slug;
    
    @Column(name = "title", updatable = true, nullable = false)
    public String title;
    
    @Column(name = "location", updatable = true, nullable = true)
    public String location;
    
    @Column(name="description", updatable = true, nullable = true)
	public String description;
    
    @Column(name="has_meal", updatable = true, nullable = false)
    public boolean has_meal;
    
    @Enumerated(EnumType.STRING)
    @Column(name="type", updatable = false, nullable = false)
    public PollType type;

    @Temporal(TemporalType.DATE)
    @Column(name="created_at", updatable = false, nullable = false)
    public Date created_at;
    
    @ManyToMany(mappedBy="listPolls")
    @Column(name="listUsers", updatable = false, nullable = true)
    public List<User> listUsers;
    
    @ManyToOne
    @JoinColumn(name="user_id")
    public User admin;
    
    @Column(name="comments", updatable = true, nullable = true)
    @OneToMany(mappedBy = "poll")
    public List<Comment> comments;
    
    @Column(name="choices", updatable = true, nullable = true)
    @OneToMany(mappedBy = "poll")
    public List<Choice > choices;				// Liste des choix creer par l'admin du poll
    

    public Poll(String title, String location, String description, boolean has_meal, PollType type, List<User> users) {
        this.slug = Utils.generateSlug(24);
        this.title = title;
        this.location = location;
        this.description = description;
        this.has_meal = has_meal;
        this.type = type;
        this.created_at = new Date();
        choices = new ArrayList<Choice>();
        comments = new ArrayList<Comment>();
        listUsers = new ArrayList<User>();
        listUsers.add(admin);
        if(users!=null && users.size()>0) {
            for( User user: users) {
            	listUsers.add(user);
            }
        }
    }
    
    public void addUser(User user) {
    	this.listUsers.add(user);
//    	user.AddPoll(this);
    }
    
    public void addChoice(Choice choice) {
    	this.choices.add(choice);
    }
    
    public void addComment(Comment comment) {
    	this.comments.add(comment);
    }



}

