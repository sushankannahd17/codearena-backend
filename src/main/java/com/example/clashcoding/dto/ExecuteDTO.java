package com.example.clashcoding.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteDTO {
    private String language;

    private String version;

    private List<FileDTO> files;

    private String stdin;
}
