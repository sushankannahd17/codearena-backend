package com.example.clashcoding.controller;

import com.example.clashcoding.dto.EmailDTO;
import com.example.clashcoding.dto.UserDTO;
import com.example.clashcoding.service.UserService;
import com.example.clashcoding.utils.JwtUtils;
import com.example.clashcoding.utils.ResponseBody;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserController {

	@Autowired
	private UserService service;

	@Autowired
	private JwtUtils jwtUtils;

	@PostMapping("/signup")
	public ResponseEntity<?> signUp(@RequestBody UserDTO dto) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseBody("success", service.signUp(dto)));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseBody("error", e.getMessage()));
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserDTO dto, HttpServletResponse response) {
		try {
			Long userID = service.login(dto);

			String token = jwtUtils.generateToken(userID);

			Cookie cookie = new Cookie("jwt", token);
			cookie.setHttpOnly(true);
			cookie.setSecure(false);
			cookie.setPath("/");
			cookie.setMaxAge(60 * 60 * 5);

			response.addCookie(cookie);

			return ResponseEntity
				.status(HttpStatus.ACCEPTED)
				.body(new ResponseBody("success", "User logged in successfully"));
		} catch (Exception e) {
			if (
				e.getMessage().equalsIgnoreCase("Incorrect Password") ||
				e.getMessage().equalsIgnoreCase("User Does Not Exists")
			) return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.body(new ResponseBody("error", e.getMessage())); else return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}

	@PostMapping("/sendotp")
	public ResponseEntity<?> sendOTP(@RequestBody EmailDTO dto) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseBody("success", service.sendOTP(dto)));
		} catch (Exception e) {
			if (e.getMessage().equals("User does not exist")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseBody("error", e.getMessage()));
			}

			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}

	@PostMapping("/changepassword")
	public ResponseEntity<?> changePassword(@RequestBody EmailDTO dto) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseBody("success", service.changePassword(dto)));
		} catch (Exception e) {
			if (e.getMessage().equals("OTP is invalid")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseBody("error", e.getMessage()));
			}

			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}
}
