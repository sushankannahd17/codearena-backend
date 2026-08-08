package com.example.codearena.controller;

import com.example.codearena.dto.EmailDTO;
import com.example.codearena.dto.UserDTO;
import com.example.codearena.service.UserService;
import com.example.codearena.utils.ID;
import com.example.codearena.utils.JwtUtils;
import com.example.codearena.utils.ResponseBody;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
public class UserController {

	@Autowired
	private UserService service;

	@Autowired
	private ID ID;

	@Autowired
	private JwtUtils jwtUtils;

	@PostMapping("/signup")
	public ResponseEntity<?> signUp(@RequestBody UserDTO dto) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseBody("success", service.signUp(dto)));
		} catch (Exception e) {
			e.printStackTrace();
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
			cookie.setMaxAge(60 * 60 * 5 * 100);

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

	@GetMapping("/all")
	public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
		try {
			Long id = ID.getId(request);

			return ResponseEntity.status(HttpStatus.OK).body(new ResponseBody("success", service.getUsers(id)));
		} catch (Exception e) {
			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}

	@GetMapping("/getProfile")
	public ResponseEntity<?> getProfile(HttpServletRequest request) {
		try {
			Long id = ID.getId(request);

			return ResponseEntity.status(HttpStatus.OK).body(new ResponseBody("success", service.getProfile(id)));
		} catch (Exception e) {
			if (e.getMessage().equals("User is not authenticated")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseBody("error", e.getMessage()));
			}

			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletResponse response) {
		try {
			Cookie cookie = new Cookie("jwt", "");
			cookie.setHttpOnly(true);
			cookie.setSecure(false);
			cookie.setPath("/");
			cookie.setMaxAge(0);

			response.addCookie(cookie);

			return ResponseEntity.status(HttpStatus.OK).body(new ResponseBody("success", "Logged out successfully"));
		} catch (Exception e) {
			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseBody("error", e.getMessage()));
		}
	}

	@GetMapping("/generate-template")
	public ResponseEntity<?> downloadTemplate() {
		try {
			byte[] excel = service.downloadTemplate();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

			headers.setContentDisposition(ContentDisposition.attachment().filename("Users Template.xlsx").build());

			return ResponseEntity.status(HttpStatus.OK).headers(headers).body(excel);
		} catch (Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseBody("error", e.getMessage()));
		}
	}

	@PostMapping("/add-users")
	public ResponseEntity<?> addUsers(@RequestParam("file")MultipartFile file, HttpServletRequest request) {
		try {
			Long id = ID.getId(request);

			return ResponseEntity.status(HttpStatus.OK).body(new ResponseBody("success", service.addUsers(file, id)));
		} catch (Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseBody("error", e.getMessage()));
		}
	}
}
