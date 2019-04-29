package models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import utils.Utils;

@Entity
@Data
@ToString
@NoArgsConstructor
public class Poll implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;    
    @Column(name = "slug", updatable = false, nullable = false)
    private String slug;
    @Column(name = "title", updatable = true, nullable = false)
    private String title;
    @Column(name = "location", updatable = true, nullable = true)
    private String location;
    @Column(name="description", updatable = true, nullable = true)
    private String description;
    @Column(name="description", updatable = true, nullable = false)
    private boolean has_meal;
    
    @Enumerated(EnumType.STRING)
    @Column(name="type", updatable = false, nullable = false)
    private PollType type;

    @Temporal(TemporalType.DATE)
    @Column(name="created_at", updatable = false, nullable = false)
    private Date created_at;
    
    @ManyToMany(mappedBy="listPolls")
    @Column(name="listUsers", updatable = false, nullable = false)
    private List<User> listUsers;
    @ManyToOne
    @Column(name="admin", updatable = false, nullable = false)
    private User admin;
    @Column(name="comments", updatable = true, nullable = true)
    @OneToMany(mappedBy = "poll")
    private List<Comment> comments;
    @Column(name="choices", updatable = true, nullable = false)
    @OneToMany(mappedBy = "poll")
    private List<Choice> choices;

    public Poll(String title, String location, String description, boolean has_meal, PollType type, Date created_at) {
        this.slug = Utils.generateSlug(24);
        this.title = title;
        this.location = location;
        this.description = description;
        this.has_meal = has_meal;
        this.type = type;
        this.created_at = created_at;
    }
}

