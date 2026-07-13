package com.example.clashcoding.response;

import com.example.clashcoding.models.Question;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContestEnterResponse {

	public List<Question> questionList;

	public Instant sessionEnd;

	public Long secondsLeft;

	public String status;
}
