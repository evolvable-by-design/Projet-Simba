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
    private String username;
    private String password;
    private String first_name;
    private String last_name;
    private String email;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date created_at;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated_at;
    private boolean is_temp;
    
    @OneToOne
    private MealPreference meal_preference;
   
    
    @OneToMany(mappedBy = "user")
    private List<Comment> comments;
   
    @ManyToMany
    private List<Poll> listPolls;
    @OneToMany(mappedBy = "user")
    private List<Choice> choices;

    public User(String username, String password, String first_name, String last_name, String email, Date created_at, Date updated_at, boolean is_temp, MealPreference preference) {
        this.username = username;
        this.password = password;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.is_temp = is_temp;
        this.meal_preference = preference;
        listPolls = new ArrayList<Poll>();
        comments = new ArrayList<Comment>();
    }
}
