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
    @Column(name = "text", updatable = true, nullable = true)
    private String text;
    @Temporal(TemporalType.DATE)
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date created_at;

    @ManyToOne
    private User user;
    @ManyToOne
    private Poll poll;
  
      public Comment(String text) {
        this.text = text;
        this.created_at = new Date();

    }
}
