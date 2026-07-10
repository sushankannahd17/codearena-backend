package com.example.clashcoding.dto;

import com.example.clashcoding.models.Question;
import com.example.clashcoding.models.User;
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

	ContestDTO(List<Long> questionID, List<Long> attenderID) {
		this.questionID = questionID;
		this.attenderID = attenderID;
	}
}
