package com.example.codearena.service;

import com.example.codearena.response.ExecuteResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class JudgeService {

	@Value("${judge.url}")
	private String judgeURL;

	@Autowired
	private ExecutorService executorService;

	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper mapper = new ObjectMapper();

	private static final Map<String, String> languageExtMap = Map.of(
			"python", "py",
			"java", "java",
			"cpp", "cpp",
			"javascript", "js"
	);

	public Future<ExecuteResponse> executeAsync(String code, String language, String input, Long runTime) {
		return executorService.submit(() -> execute(code, language, input, runTime));
	}

	public ExecuteResponse execute(String code, String language, String input, Long runTime) throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("language", language);
		requestBody.put("version", "*");
		requestBody.put("stdin", input);
		requestBody.put("run_timeout", runTime);

		requestBody.put("files", List.of(
				Map.of(
						"name", "main." + languageExtMap.get(language),
						"content", code
				)
		));

		HttpEntity<Map<String, Object>> request =
				new HttpEntity<>(requestBody, headers);


			ResponseEntity<String> response = restTemplate.postForEntity(
					judgeURL,
					request,
					String.class
			);

			return mapper.readValue(response.getBody(), ExecuteResponse.class);
	}
}