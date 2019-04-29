package models;

import lombok.*;
import utils.Utils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@ToString
@NoArgsConstructor
public class Poll implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;
    private String slug;
    private String title;
    private String location;
    private String description;
    private boolean has_meal;
    @Enumerated(EnumType.STRING)
    private PollType type;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created_at;

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

