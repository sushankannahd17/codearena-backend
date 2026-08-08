package com.example.codearena.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	@JsonIgnore
	String password;

	@Column(nullable = false)
	Boolean available;
}
