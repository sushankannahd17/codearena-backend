package com.example.codearena.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionResponseDTO {

	private Long id;
	private String title;
	private String description;
	private List<ExamplesIODTO> examplesIO;
	private List<TestCasesDTO> testcases;
	private Long runTime;
}
