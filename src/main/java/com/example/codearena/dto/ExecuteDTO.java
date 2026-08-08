package com.example.codearena.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteDTO {

	private String language;

	private String version;

	private List<FileDTO> files;

	private String stdin;

	private Integer run_timeout;
}
