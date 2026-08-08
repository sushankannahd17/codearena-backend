package com.example.codearena.response;

import com.example.codearena.models.Question;
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

	public Integer resumeCount;
}
