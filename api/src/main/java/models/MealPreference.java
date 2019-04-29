package models;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@ToString
@NoArgsConstructor
public class MealPreference implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;
    @Column(name="text", updatable = true, nullable = false)
    private String text;
    
    @OneToOne(mappedBy = "meal_preference")
    private User user;
    
    public MealPreference(String text) {
        this.text = text;
    }
}
