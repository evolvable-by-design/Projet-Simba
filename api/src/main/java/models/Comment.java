package models;


import javax.persistence.*;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import java.io.Serializable;
import java.util.Date;

@Entity

public class Comment extends PanacheEntity implements Serializable {

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
	@JoinColumn(name="user_id")
	private User user;
	@ManyToOne
	@JoinColumn(name="poll_id")
	private Poll poll;

	public Comment(String text) {
		this.text = text;
		this.created_at = new Date();

	}
}
