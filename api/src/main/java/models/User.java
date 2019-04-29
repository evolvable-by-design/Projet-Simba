package models;

import java.io.Serializable;
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

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@NoArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;
    @Column(name="username", updatable = true, nullable = false)
    private String username;
    @Column(name="password", updatable = true, nullable = false)
    private String password;
    @Column(name="first_name", updatable = true, nullable = false)
    private String first_name;
    @Column(name="last_name", updatable = true, nullable = false)
    private String last_name;
    @Column(name="email", updatable = true, nullable = false)
    private String email;

    @Temporal(TemporalType.DATE)
    @Column(name="created_at", updatable = false, nullable = false)
    private Date created_at;
    @Temporal(TemporalType.DATE)
    @Column(name="updtated_at", updatable = true, nullable = false)
    private Date updated_at;
    @Column(name="is_temp", updatable = true, nullable = false)
    private boolean is_temp;

    @OneToOne
    private MealPreference meal_preference;
   
    
    @OneToMany(mappedBy = "user")
    private List<Comment> comments;
   
    @ManyToMany
    private List<Poll> listPolls;
    @OneToMany(mappedBy = "user")
    private List<Choice> choices;

    public User(String username, String password, String first_name, String last_name, String email, boolean is_temp, MealPreference preference) {

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

    }
}
