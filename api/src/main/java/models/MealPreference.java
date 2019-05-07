package models;


import javax.persistence.*;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import java.io.Serializable;

@Entity

public class MealPreference extends PanacheEntity implements Serializable {

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
