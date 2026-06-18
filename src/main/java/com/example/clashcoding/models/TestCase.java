package com.example.clashcoding.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
@Table(name = "testcase")
public class TestCase {

	@Id
	@GeneratedValue
	public Long id;

	@NotBlank
	public String input;

	@NotBlank
	public String output;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "problem_id")
	public Question question;
}
