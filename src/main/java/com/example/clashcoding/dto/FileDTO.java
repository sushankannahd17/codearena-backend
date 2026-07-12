package com.example.clashcoding.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO {
    private String name;
    public String content;

    public FileDTO(String content) {this.content = content;}
}
