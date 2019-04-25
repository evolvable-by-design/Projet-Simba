package models;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@ToString
@NoArgsConstructor
public class Comment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;
    private String text;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created_at;

    public Comment(String text, Date created_at) {
        this.text = text;
        this.created_at = created_at;
    }
}
