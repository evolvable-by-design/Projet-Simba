package models;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Data
@ToString
@NoArgsConstructor
public class Choice implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;
    @Column(name = "name", updatable = true, nullable = false)
    private String name;
    @Column(name = "full_day", updatable = true, nullable = false)
    private boolean full_day;
    
    @Column(name = "start_date", updatable = false, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date start_date;
    @Column(name = "end_date", updatable = false, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date end_date;
    @Column(name = "votes_limit", updatable = false, nullable = false)
    private int votes_limit;
    @Column(name = "poll", updatable = false, nullable = false)
    @ManyToOne
    private Poll poll;
    @Column(name = "user", updatable = false, nullable = false)
    @ManyToOne
    private User user;
    
    public Choice(String name, boolean full_day, Date start_date, Date end_date, int votes_limit) {
        this.name = name;
        this.full_day = full_day;
        this.start_date = start_date;
        this.end_date = end_date;
        this.votes_limit = votes_limit;
    }
}

