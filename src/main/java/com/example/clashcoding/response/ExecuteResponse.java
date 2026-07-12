package com.example.clashcoding.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteResponse {
    private RunResponse run;
    private String language;
    private String version;
}
