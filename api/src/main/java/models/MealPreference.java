package models;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity

public class MealPreference extends PanacheEntityBase  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    public long id;
    
    @Column(name="preference", updatable = true, nullable = false)
    public String preference;
    
    @OneToOne(mappedBy = "meal_preference")
    public UserEntity user;
    public MealPreference() {
    	super();
    }
    
    public MealPreference(String preference) {
        this.preference = preference;
    }
    

}
