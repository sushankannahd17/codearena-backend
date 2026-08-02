package com.example.clashcoding.controller;

import com.example.clashcoding.dto.ContestDTO;
import com.example.clashcoding.dto.ResumeDTO;
import com.example.clashcoding.dto.SubmitDTO;
import com.example.clashcoding.service.ContestService;
import com.example.clashcoding.utils.ID;
import com.example.clashcoding.utils.JwtUtils;
import com.example.clashcoding.utils.ResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contest")
public class ContestController {

	@Autowired
	private ContestService service;

	@Autowired
	private ID ID;

	@Autowired
	private JwtUtils jwtUtils;

	@PostMapping("/create")
	public ResponseEntity<?> createContest(@RequestBody ContestDTO dto, HttpServletRequest request) {
		try {
			Long id = ID.getId(request);

			return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(new ResponseBody("success", service.createContest(dto, id)));
		} catch (Exception e) {
			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}

	@GetMapping("/getcreatedcontests")
	public ResponseEntity<?> getContests(HttpServletRequest request) {
		try {
			Long id = ID.getId(request);

			return ResponseEntity.status(HttpStatus.OK).body(new ResponseBody("success", service.getContests(id)));
		} catch (Exception e) {
			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}

	@GetMapping("/viewavailablecontests")
	public ResponseEntity<?> viewAvailableContests(HttpServletRequest request) {
		try {
			Long id = ID.getId(request);

			return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseBody("success", service.viewAvailableContests(id)));
		} catch (Exception e) {
			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}

	@DeleteMapping("/deletecontest/{contestID}")
	public ResponseEntity<?> deleteContest(HttpServletRequest request, @PathVariable Long contestID) {
		try {
			Long id = ID.getId(request);

			return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseBody("success", service.deleteContest(id, contestID)));
		} catch (Exception e) {
			if (e.getMessage().equals("User is not authenticated") || e.getMessage().equals("User is not authorized")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseBody("error", e.getMessage()));
			}

			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}

	@PatchMapping("/modify/{contestID}")
	public ResponseEntity<?> modifyQuestions(
		@PathVariable Long contestID,
		HttpServletRequest request,
		@RequestBody ContestDTO dto
	) {
		try {
			Long id = ID.getId(request);

			return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseBody("success", service.modifyQuestions(contestID, id, dto)));
		} catch (Exception e) {
			if (e.getMessage().equals("User is not authenticated") || e.getMessage().equals("User is not authorized")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseBody("error", e.getMessage()));
			}

			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}

	@GetMapping("/{contestID}")
	public ResponseEntity<?> contestDetails(@PathVariable Long contestID, HttpServletRequest request) {
		try {
			Long userID = ID.getId(request);

			return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseBody("success", service.getContest(contestID, userID)));
		} catch (Exception e) {
			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}

	@PostMapping("/{contestID}/entercontest")
	public ResponseEntity<?> validateAndEnter(@PathVariable Long contestID, HttpServletRequest request) {
		try {
			Long userID = ID.getId(request);

			return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseBody("success", service.validateAndEnter(contestID, userID)));
		} catch (Exception e) {
			if (e.getMessage().equals("User has exceeded the resume count")) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseBody("error", e.getMessage()));
			}

			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}

	@PostMapping("/{contestID}/run")
	public ResponseEntity<?> runCode(
			@PathVariable Long contestID,
			HttpServletRequest request,
			@RequestBody SubmitDTO dto
	) {
		try {
			Long userID = ID.getId(request);

			return ResponseEntity
					.status(HttpStatus.OK)
					.body(
							new ResponseBody(
									"success",
									service.runCode(dto.getCode(), userID, contestID, dto.getQuestionID(), dto.getLanguage())
							)
					);
		} catch (Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseBody("error", e.getMessage()));
		}
	}

	@PostMapping("/{contestID}/execute")
	public ResponseEntity<?> executeCode(
		@PathVariable Long contestID,
		HttpServletRequest request,
		@RequestBody SubmitDTO dto
	) {
		try {
			Long userID = ID.getId(request);

			return ResponseEntity
				.status(HttpStatus.OK)
				.body(
					new ResponseBody(
						"success",
						service.submitCode(dto.getCode(), userID, contestID, dto.getQuestionID(), dto.getLanguage())
					)
				);
		} catch (Exception e) {
			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}

	@GetMapping("/fetch/{contestID}/{questionID}")
	public ResponseEntity<?> fetchSubmittedCode(
		@PathVariable Long contestID,
		@PathVariable Long questionID,
		HttpServletRequest request
	) {
		try {
			Long userID = ID.getId(request);

			return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseBody("success", service.fetchSubmittedCode(contestID, questionID, userID)));
		} catch (Exception e) {
			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}

	@PatchMapping("/resumecount")
	public ResponseEntity<?> modifyResumeCount(@RequestBody ResumeDTO dto, HttpServletRequest request) {
		try {
			Long userID = ID.getId(request);

			return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseBody("success", service.modifyResumeCount(dto.getContestID(), userID)));
		} catch (Exception e) {
			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}
}
