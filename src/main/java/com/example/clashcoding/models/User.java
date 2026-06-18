package com.example.clashcoding.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue
	Long id;

	@NotBlank
	String name;

	@NotBlank
	@Column(unique = true)
	String email;

	@NotBlank
	String password;
}
