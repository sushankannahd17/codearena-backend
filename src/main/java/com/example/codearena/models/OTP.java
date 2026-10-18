package com.example.codearena.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "otp")
public class OTP {

	@Id
	@GeneratedValue
	public Long id;

	@NotBlank
	public String email;

	@NotNull
	public Integer otp;
}
