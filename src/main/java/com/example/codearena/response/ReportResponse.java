package com.example.codearena.response;

import com.example.codearena.models.Question;
import com.example.codearena.models.Submission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private Long id; 
    private String contestname;
    private List<Question> questions;
    private Long minutes;
    private Instant startDate;
    private Instant endDate;
    private Integer maxResumeCount;
    private Integer resumeCount;
    private Instant userStartDate;
    private Instant userEndDate;
    private List<Submission> submissions;
    private Integer submitCount;
    private boolean completed;
    private boolean contestOver;
    private Integer solvedCount;
}
