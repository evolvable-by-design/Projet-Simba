package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class UserEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    public long id;
    
    @Column(name="username", updatable = true, nullable = false)
    public String username;
    
    @Column(name="Userpassword", updatable = true, nullable = true)
    public String password;
    
    @Column(name="first_name", updatable = true, nullable = false)
    public String first_name;
    
    @Column(name="last_name", updatable = true, nullable = false)
    public String last_name;
    @Column(name="email", updatable = true, nullable = false)
    public String email;

    @Temporal(TemporalType.DATE)
    @Column(name="created_at", updatable = false, nullable = false)
    @CreationTimestamp				//met la date courante à la creation
    public Date created_at;
    
    @Temporal(TemporalType.DATE)
    @Column(name="updated_at", updatable = true, nullable = false)
    @UpdateTimestamp				//met la date d'update		
    public Date updated_at;
    
    @Column(name="is_temp", updatable = true, nullable = false)
    public boolean is_temp;

    @OneToOne
    public MealPreference meal_preference;
   
    
    @OneToMany(mappedBy = "user")
    public List<Comment> comments;
   
    @ManyToMany
    public List<Poll> listPolls;				//liste des polls auquels il a participe
    @OneToMany(mappedBy = "user")
    public List<Choice> choices;   			//Liste des choix fait par le user

    public UserEntity() {
    	super();
    }
    
    public UserEntity(String username, String password, String first_name, String last_name, String email, boolean is_temp, MealPreference preference) {

        this.username = username;
        this.password = password;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.is_temp = is_temp;

        //initialisation dates
        this.created_at = new Date();
        this.updated_at = this.created_at;
      
        this.meal_preference = preference;
        listPolls = new ArrayList<Poll>();
        comments = new ArrayList<Comment>();
        choices = new ArrayList<Choice>();
    }
    
    public void addPoll(Poll poll) {
    	this.listPolls.add(poll);
    }
    
    public void addChoice(Choice choice) {
    	this.choices.add(choice);
    }
    
    public void addComment(Comment comment) {
    	this.comments.add(comment);
    }
    
}