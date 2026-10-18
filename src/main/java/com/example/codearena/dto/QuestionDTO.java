package com.example.codearena.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {

	public String title;
	public String description;
	public List<ExamplesIODTO> examplesIO = new ArrayList<>();
	public List<TestCasesDTO> testcases = new ArrayList<>();
	public Long runTime;
	public Long userID;
}
