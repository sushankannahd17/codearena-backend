package com.example.clashcoding.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "submissions", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "question_id" }))
public class Submission {

	@Id
	@GeneratedValue
	public long id;

	@OneToOne
	@JoinColumn(name = "question_id")
	public Question questionID;

	@ManyToOne
	@JoinColumn(name = "user_id")
	public User user;

	@Column(columnDefinition = "TEXT")
	public String code;

	public String verdict;

	public String language;

	public int passedTC;

	public int totalTC;
}
