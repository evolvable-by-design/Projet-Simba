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
    private String username;
    private String password;
    private String first_name;
    private String last_name;
    private String email;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created_at;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated_at;
    private boolean is_temp;
    private String preference;

    public User(String username, String password, String first_name, String last_name, String email, Date created_at, Date updated_at, boolean is_temp, String preference) {
        this.username = username;
        this.password = password;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.is_temp = is_temp;
        this.preference = preference;
    }
}
