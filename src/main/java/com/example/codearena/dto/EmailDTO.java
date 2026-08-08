package com.example.codearena.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDTO {

	private String recipient;
	private int otp;
	private String password;

	EmailDTO(String recipient) {
		this.recipient = recipient;
	}
}
