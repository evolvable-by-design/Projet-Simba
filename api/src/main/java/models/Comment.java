package models;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity

public class Comment extends PanacheEntityBase {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	public long id;
	
	@Column(name = "comment", updatable = true, nullable = true)
	public String comment;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "created_at", updatable = false, nullable = false)
	public Date created_at;

	@ManyToOne
	@JoinColumn(name="userId")
	public UserEntity user;
	
	@ManyToOne
	@JoinColumn(name="poll_id")
	public Poll poll;

	public Comment(String comment) {
		this.comment = comment;
		this.created_at = new Date();

	}
}
