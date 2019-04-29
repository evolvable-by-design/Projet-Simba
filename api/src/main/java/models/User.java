package models;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@ToString
@NoArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;
    @Column(name="username", updatable = true, nullable = false)
    private String username;
    @Column(name="password", updatable = true, nullable = false)
    private String password;
    @Column(name="first_name", updatable = true, nullable = false)
    private String first_name;
    @Column(name="last_name", updatable = true, nullable = false)
    private String last_name;
    @Column(name="email", updatable = true, nullable = false)
    private String email;
    @Temporal(TemporalType.DATE)
    @Column(name="created_at", updatable = false, nullable = false)
    private Date created_at;
    @Temporal(TemporalType.DATE)
    @Column(name="updtated_at", updatable = true, nullable = false)
    private Date updated_at;
    @Column(name="is_temp", updatable = true, nullable = false)
    private boolean is_temp;
    @Column(name="preference", updatable = true, nullable = true)
    private String preference;

    public User(String username, String password, String first_name, String last_name, String email, boolean is_temp, String preference) {
        this.username = username;
        this.password = password;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.is_temp = is_temp;
        this.preference = preference;

        //initialisation dates
        this.created_at = new Date();
        this.updated_at = this.created_at;
    }
}
