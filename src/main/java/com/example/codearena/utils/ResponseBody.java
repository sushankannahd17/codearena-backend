package com.example.codearena.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseBody {

	private String status;
	private Object message;
}
