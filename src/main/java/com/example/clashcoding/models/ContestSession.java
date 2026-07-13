package com.example.clashcoding.models;

import com.example.clashcoding.models.Question;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
	private Contest contestId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userID", nullable = false)
	private User userID;

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

	@Column(name = "status")
	private String status;

	@PrePersist
	private void prepersist() {
		Instant now = Instant.now();
		sessionStart = now;
	}
}
