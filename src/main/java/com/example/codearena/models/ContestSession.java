package com.example.codearena.models;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "contestsession")
public class ContestSession {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contestId", nullable = false)
	private Contest contest;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userID", nullable = false)
	private User user;

	@Column(name = "sessionStart")
	private Instant sessionStart;

	@Column(name = "sessionEnd")
	private Instant sessionEnd;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		name = "contestseesion_question",
		joinColumns = @JoinColumn(name = "id"),
		inverseJoinColumns = @JoinColumn(name = "questionID")
	)
	private List<Question> questions;

	@Column(name = "submitcount")
	private Integer submitCount;

	@Column(name = "resumecount")
	private Integer resumeCount;

	@Column(name = "completed")
	private boolean completed;

	@PrePersist
	private void prepersist() {
		Instant now = Instant.now();
		sessionStart = now;
	}
}
