package com.example.codearena.controller;

import com.example.codearena.dto.QuestionDTO;
import com.example.codearena.dto.TestCasesDTO;
import com.example.codearena.security.AuthenticatedUserService;
import com.example.codearena.service.QuestionService;
import com.example.codearena.utils.ID;
import com.example.codearena.utils.JwtUtils;
import com.example.codearena.utils.ResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
	private final QuestionService service;
	private final AuthenticatedUserService authenticatedUserService;

	QuestionController(QuestionService questionService, JwtUtils jwtUtils, AuthenticatedUserService authenticatedUserService) {
		this.service = questionService;
		this.authenticatedUserService = authenticatedUserService;
	}

	@PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
	@PostMapping("/create")
	public ResponseEntity<?> createQuestion(@RequestBody QuestionDTO dto, HttpServletRequest request) {
		try {
			Long id = authenticatedUserService.getCurrentUserId();

			dto.setUserID(id);

			return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(new ResponseBody("success", "Question created. ID: " + service.createQuestion(dto)));
		} catch (Exception e) {
			if (e.getMessage().equals("User is not authenticated")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseBody("error", e.getMessage()));
			}

			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}

	@PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
	@PutMapping("/{questionID}")
	public ResponseEntity<?> createQuestion(@RequestBody QuestionDTO dto, HttpServletRequest request, @PathVariable Long questionID) {
		try {
			Long id = authenticatedUserService.getCurrentUserId();

			dto.setUserID(id);

			return ResponseEntity
					.status(HttpStatus.CREATED)
					.body(new ResponseBody("success", service.modifyQuestion(dto, questionID)));
		} catch (Exception e) {
			if (e.getMessage().equals("User is not authenticated")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseBody("error", e.getMessage()));
			}

			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseBody("error", e.getMessage()));
		}
	}

	@PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
	@PatchMapping("/{questionID}/testcases")
	public ResponseEntity<?> modifyTestCases(
		@PathVariable Long questionID,
		@RequestBody List<TestCasesDTO> dto,
		HttpServletRequest request
	) {
		try {
			Long id = authenticatedUserService.getCurrentUserId();

			return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseBody("success", service.modifyTestCases(questionID, dto, id)));
		} catch (Exception e) {
			if (e.getMessage().equals("User is not authenticated") || e.getMessage().equals("User is not authorized")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseBody("error", e.getMessage()));
			}

			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}

	@PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
	@DeleteMapping("/{questionID}")
	public ResponseEntity<?> deleteQuestion(@PathVariable Long questionID, HttpServletRequest request) {
		try {
			Long id = authenticatedUserService.getCurrentUserId();

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

	@PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
	@GetMapping("/get")
	public ResponseEntity<?> getQuestions(HttpServletRequest request) {
		try {
			Long id = authenticatedUserService.getCurrentUserId();

			return ResponseEntity.status(HttpStatus.OK).body(new ResponseBody("success", service.getQuestions(id)));
		} catch (Exception e) {
			if (e.getMessage().equals("User is not authenticated")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseBody("error", e.getMessage()));
			}

			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}
}
