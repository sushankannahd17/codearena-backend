package com.example.codearena.models;

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
	@JoinTable(name="contest_attenders", joinColumns = @JoinColumn(name = "contest_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	public List<User> attenders;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="contest_questions", joinColumns = @JoinColumn(name = "contest_id"), inverseJoinColumns = @JoinColumn(name = "question_id"))
	public List<Question> questions;

	@Column
	public Long minutes;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userID")
	public User user;

	@Column(name = "start_date")
	public Instant startDate;

	@Column(name = "end_date")
	public Instant endDate;

	@Column(name = "resumeCount")
	private Integer maxResumeCount;

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
