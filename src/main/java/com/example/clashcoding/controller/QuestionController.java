package com.example.clashcoding.controller;

import com.example.clashcoding.config.ExceptionConfig;
import com.example.clashcoding.dto.QuestionDTO;
import com.example.clashcoding.dto.TestCasesDTO;
import com.example.clashcoding.models.TestCase;
import com.example.clashcoding.service.QuestionService;
import com.example.clashcoding.utils.JwtUtils;
import com.example.clashcoding.utils.ResponseBody;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/questions")
public class QuestionController {

	@Autowired
	private QuestionService service;

	@Autowired
	private JwtUtils jwtUtils;

	@PostMapping("/create")
	public ResponseEntity<?> createQuestion(@RequestBody QuestionDTO dto, HttpServletRequest request) {
		try {
			Cookie[] cookies = request.getCookies();

			Long id = null;
			for (Cookie cookie : cookies) {
				if ("jwt".equalsIgnoreCase(cookie.getName())) {
					String token = cookie.getValue();

					id = Long.parseLong(jwtUtils.extractID(token));
					break;
				}
			}

			if (id == null) {
				throw new ExceptionConfig("User is not authenticated");
			}

			dto.setUserID(id);

			return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(new ResponseBody("success", "Question created. ID: " + service.createQuestion(dto)));
		} catch (Exception e) {
			if (e.getMessage().equals("User is not authenticated")) {
				return ResponseEntity
						.status(HttpStatus.UNAUTHORIZED)
						.body(new ResponseBody("error", e.getMessage()));
			}

			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}

	@PatchMapping("/{questionID}/testcases")
	public ResponseEntity<?> modifyTestCases(
		@PathVariable Long questionID,
		@RequestBody List<TestCasesDTO> dto,
		HttpServletRequest request
	) {
		try {
			Cookie[] cookies = request.getCookies();

			Long id = null;
			for (Cookie cookie : cookies) {
				if ("jwt".equalsIgnoreCase(cookie.getName())) {
					String token = cookie.getValue();

					id = Long.parseLong(jwtUtils.extractID(token));
					break;
				}
			}

			if (id == null) {
				throw new ExceptionConfig("User is not authenticated");
			}

			return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseBody("success", service.modifyTestCases(questionID, dto, id)));
		} catch (Exception e) {
			if (e.getMessage().equals("User is not authenticated") || e.getMessage().equals("User is not authorized")) {
				return ResponseEntity
						.status(HttpStatus.UNAUTHORIZED)
						.body(new ResponseBody("error", e.getMessage()));
			}

			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}

	@DeleteMapping("/{questionID}")
	public ResponseEntity<?> deleteQuestion(@PathVariable Long questionID, HttpServletRequest request) {
		try {
			Cookie[] cookies = request.getCookies();

			Long id = null;
			for (Cookie cookie : cookies) {
				if ("jwt".equalsIgnoreCase(cookie.getName())) {
					String token = cookie.getValue();

					id = Long.parseLong(jwtUtils.extractID(token));
					break;
				}
			}

			if (id == null) {
				throw new ExceptionConfig("User is not authenticated");
			}

			return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseBody("success", service.deleteQuestion(questionID, id)));
		} catch (Exception e) {
			if (e.getMessage().equals("User is not authenticated") || e.getMessage().equals("User is not authorized")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseBody("error", e.getMessage()));
			}

			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}

	@GetMapping("/get")
	public ResponseEntity<?> getQuestions(HttpServletRequest request) {
		try {
			Cookie[] cookies = request.getCookies();

			Long id = null;

			for (Cookie cookie : cookies) {
				if ("jwt".equalsIgnoreCase(cookie.getName())) {
					String token = cookie.getValue();

					id = Long.parseLong(jwtUtils.extractID(token));
					break;
				}
			}

			if (id == null) {
				throw new ExceptionConfig("User is not authenticated");
			}

			return ResponseEntity.status(HttpStatus.OK).body(new ResponseBody("success", service.getQuestions(id)));
		} catch (Exception e) {
			if (e.getMessage().equals("User is not authenticated")) {
				return ResponseEntity
						.status(HttpStatus.UNAUTHORIZED)
						.body(new ResponseBody("error", e.getMessage()));
			}

			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}
}
