package com.example.codearena.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Data
@Table(name = "questions")
public class Question {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	@NotBlank
	@Column(unique = true)
	public String title;

	@NotBlank
	@Column(length = 10000)
	public String description;

	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	public List<ExamplesIO> examples = new ArrayList<>();

	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	public List<TestCase> tc = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "userID", referencedColumnName = "id")
	public User user;

	@CreatedDate
	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@CreatedDate
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	private Long runTime;

	@PrePersist
	public void prepersist() {
		Instant now = Instant.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	public void preupdate() {
		updatedAt = Instant.now();
	}
}
