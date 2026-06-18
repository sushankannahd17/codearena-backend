package com.example.clashcoding.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    public String title;
    public String description;
    public List<ExamplesIODTO> examplesIO = new ArrayList<>();
    public List<TestCasesDTO> testcases = new ArrayList<>();
    public Long userID;
}
