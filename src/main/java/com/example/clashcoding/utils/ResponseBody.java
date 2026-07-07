package com.example.clashcoding.utils;

import com.example.clashcoding.models.Question;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseBody {

	private String status;
	private Object message;
}
