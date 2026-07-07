package com.example.clashcoding.models;

import com.example.clashcoding.models.Question;
import com.example.clashcoding.models.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Data
@Table(name = "contests")
public class Contest {

	@Id
	@GeneratedValue
	public Long id;

	@Column
	public String contestname;

	@ManyToMany(fetch = FetchType.LAZY)
	public List<User> attenders;

	@ManyToMany(fetch = FetchType.LAZY)
	public List<Question> questions;

	@Column
	public Long minutes;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userID")
	public User user;

	@Column(name = "start_date")
	public Instant startDate;

	@Column(name = "end_date")
	public Instant endDate;

	@CreatedDate
	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@PrePersist
	public void prepersist() {
		Instant now = Instant.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	public void preupdate() {
		Instant now = Instant.now();
		updatedAt = now;
	}
}
