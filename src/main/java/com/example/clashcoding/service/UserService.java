package com.example.clashcoding.service;

import com.example.clashcoding.config.ExceptionConfig;
import com.example.clashcoding.dto.EmailDTO;
import com.example.clashcoding.dto.UserDTO;
import com.example.clashcoding.models.OTP;
import com.example.clashcoding.models.User;
import com.example.clashcoding.repository.OTPRepository;
import com.example.clashcoding.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OTPRepository otpRepository;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${spring.mail.username}")
	private String sender;

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

	@Transactional
	public String sendOTP(EmailDTO dto) {
		String email = dto.getRecipient().trim().toLowerCase();

		User user = userRepository.findByEmail(email);

		if (user == null) {
			throw new ExceptionConfig("User does not exist");
		}

		otpRepository.deleteAllByEmail(email);

		SimpleMailMessage mail = new SimpleMailMessage();

		mail.setFrom(sender);
		mail.setTo(dto.getRecipient());
		mail.setSubject("Code for resetting the password");

		Random r = new Random();
		int val = r.nextInt(1000000, 10000000);
		mail.setText("Your OTP is " + val + "\nUse this to reset your password");

		javaMailSender.send(mail);

		OTP otp = new OTP();
		otp.setEmail(email);
		otp.setOtp(val);

		otpRepository.save(otp);

		return ("Mail sent successfully to " + dto.getRecipient());
	}

	@Transactional
	public String changePassword(EmailDTO dto) {
		String email = dto.getRecipient().trim().toLowerCase();

		OTP otp = otpRepository.findAllByEmail(email);

		if (dto.getOtp() != otp.getOtp()) {
			throw new ExceptionConfig("OTP is invalid");
		}

		OTP oldOtp = otpRepository.findAllByEmail(email);
		if (oldOtp != null) {
			otpRepository.delete(oldOtp);
		}

		User user = userRepository.findByEmail(email);

		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		userRepository.save(user);

		return "Password changed successfully";
	}

	public Iterable<User> getUsers() {
		return userRepository.findAll();
	}

	public User getProfile(Long userID) {
		return userRepository.findById(userID).orElseThrow();
	}
}
