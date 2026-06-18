package com.example.clashcoding.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

	@NotBlank(message = "Name is needed")
	public String name;

	@NotBlank(message = "Email is needed")
	@Email(message = "Email should be valid")
	public String email;

	@NotBlank(message = "Password is needed")
	public String password;
}
