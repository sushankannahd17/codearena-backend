package com.example.clashcoding.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitDTO {
    public String language;
    public Long questionID;
    public String code;
}
