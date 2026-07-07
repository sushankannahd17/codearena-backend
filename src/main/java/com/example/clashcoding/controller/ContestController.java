package com.example.clashcoding.controller;

import com.example.clashcoding.config.ExceptionConfig;
import com.example.clashcoding.dto.ContestDTO;
import com.example.clashcoding.service.ContestService;
import com.example.clashcoding.utils.JwtUtils;
import com.example.clashcoding.utils.ResponseBody;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contest")
public class ContestController {

	@Autowired
	private ContestService service;

	@Autowired
	private JwtUtils jwtUtils;

	@PostMapping("/create")
	public ResponseEntity<?> createContest(@RequestBody ContestDTO dto, HttpServletRequest request) {
		try {
			Cookie[] cookies = request.getCookies();
			Long id = null;

			for (Cookie cookie : cookies) {
				if ("jwt".equalsIgnoreCase(cookie.getName())) {
					id = Long.parseLong(jwtUtils.extractID(cookie.getValue()));
					break;
				}
			}

			if (id == null) {
				throw new ExceptionConfig("User is not authenticated");
			}

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
			Cookie[] cookies = request.getCookies();
			Long id = null;

			for (Cookie cookie : cookies) {
				if ("jwt".equalsIgnoreCase(cookie.getName())) {
					id = Long.parseLong(jwtUtils.extractID(cookie.getValue()));
					break;
				}
			}

			if (id == null) {
				throw new ExceptionConfig("User is not authenticated");
			}

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
			Cookie[] cookies = request.getCookies();

			Long id = null;
			for (Cookie cookie : cookies) {
				if ("jwt".equalsIgnoreCase(cookie.getName())) {
					id = Long.parseLong(jwtUtils.extractID(cookie.getValue()));
				}
			}

			return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseBody("success", service.viewAvailableContests(id)));
		} catch (Exception e) {
			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}
}
