package com.example.clashcoding.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RunResponse {
    private String stdout;

    private String stderr;

    private String output;

    private Integer code;

    private String signal;
}
