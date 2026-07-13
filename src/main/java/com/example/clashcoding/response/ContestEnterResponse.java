package com.example.clashcoding.response;

import com.example.clashcoding.models.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ContestEnterResponse {
    public List<Question> questionList;

    public Instant sessionEnd;

    public Long secondsLeft;

    public String status;
}
