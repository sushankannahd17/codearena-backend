package com.example.codearena.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultResponse {

	public String answer;
	public String input;
	public String output;
	public String expected;

	public ResultResponse(String answer) {
		this.answer = answer;
	}

	public ResultResponse(String answer, String output) {
		this.answer = answer;
		this.output = output;
	}
}
