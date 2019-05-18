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
//@Cacheable
public class Choice extends PanacheEntityBase   {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private long id;
	@Column(name = "nameChoice", updatable = true, nullable = false)
	private String name;
	@Column(name = "full_day", updatable = true, nullable = false)
	private boolean full_day;

	@Column(name = "dateDebut", updatable = false, nullable = false)
	@Temporal(TemporalType.DATE)
	private Date start_date;
	@Column(name = "end_date", updatable = false, nullable = false)
	@Temporal(TemporalType.DATE)
	private Date end_date;
	@Column(name = "votes_limit", updatable = false, nullable = false)
	private int votes_limit;
	@ManyToOne
	@JoinColumn(name="poll_id")
	private Poll poll;

	private int choiceCounter;

	@ManyToOne
	@JoinColumn(name="user_id")
	private UserEntity user;

	public Choice(String name, boolean full_day, Date start_date, Date end_date, int votes_limit) {
		this.name = name;
		this.full_day = full_day;
		this.start_date = start_date;
		this.end_date = end_date;
		this.votes_limit = votes_limit;
	}

	
	//renvoie false si plus de possibilité de faire ce choix
	public boolean increaseChoiceCounter() {
		boolean bool = false;
		if(this.choiceCounter <  this.votes_limit) {
			this.choiceCounter++;
			bool = true;
		}
		return bool;
	}


}

