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
    private String name;
    private boolean full_day;
    @Temporal(TemporalType.DATE)
    private Date start_date;
    @Temporal(TemporalType.DATE)
    private Date end_date;
    private int votes_limit;

    public Choice(String name, boolean full_day, Date start_date, Date end_date, int votes_limit) {
        this.name = name;
        this.full_day = full_day;
        this.start_date = start_date;
        this.end_date = end_date;
        this.votes_limit = votes_limit;
    }
}

