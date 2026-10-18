package com.example.codearena.dto;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContestDTO {

	private String name;
	private List<Long> attenderID;
	private List<Long> questionID;
	private Long minutes;
	private Instant startDate;
	private Instant endDate;
	private Integer maxResumeCount;

	ContestDTO(List<Long> questionID, List<Long> attenderID) {
		this.questionID = questionID;
		this.attenderID = attenderID;
	}
}
