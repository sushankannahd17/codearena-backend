package com.example.clashcoding.service;

import com.example.clashcoding.config.ExceptionConfig;
import com.example.clashcoding.dto.UserDTO;
import com.example.clashcoding.models.User;
import com.example.clashcoding.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public String signUp(UserDTO dto) {
		String email = dto.getEmail().trim().toLowerCase();

		User user = userRepository.findByEmail(email);

		if (user != null) {
			throw new ExceptionConfig("User Already Exists");
		}

		User newUser = new User();
		newUser.setName(dto.getName());
		newUser.setEmail(email);
		newUser.setPassword(passwordEncoder.encode(dto.getPassword()));

		userRepository.save(newUser);

		return "User Signed Up Successfully";
	}

	public Long login(UserDTO dto) {
		String email = dto.getEmail().trim().toLowerCase();

		User user = userRepository.findByEmail(email);

		if (user == null) {
			throw new ExceptionConfig("User Does Not Exists");
		}

		if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
			throw new ExceptionConfig("Incorrect Password");
		}

		return user.getId();
	}
}
