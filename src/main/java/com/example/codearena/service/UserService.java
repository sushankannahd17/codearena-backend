package com.example.codearena.service;

import com.example.codearena.config.ExceptionConfig;
import com.example.codearena.dto.EmailDTO;
import com.example.codearena.dto.UserDTO;
import com.example.codearena.models.OTP;
import com.example.codearena.models.User;
import com.example.codearena.models.UserRole;
import com.example.codearena.repository.OTPRepository;
import com.example.codearena.repository.UserRepository;
import jakarta.transaction.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

	@Autowired
	private EmailService mailService;

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
		newUser.setAvailable(true);
		newUser.setRole(UserRole.CANDIDATE);

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

		Random r = new Random();
		int val = r.nextInt(1000000, 10000000);

		mailService.sendEmail(dto.getRecipient(), "Code for resetting the password", "Your OTP is " + val + "\nUse this to reset your password");

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
		user.setAvailable(true);
		userRepository.save(user);

		return "Password changed successfully";
	}

	public Iterable<User> getUsers(Long userID) {
		return userRepository.findAllByIdNot(userID);
	}

	public User getProfile(Long userID) {
		return userRepository.findById(userID).orElseThrow();
	}

	public byte[] downloadTemplate() throws Exception {
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("Users");

		Row row = sheet.createRow(0);
		row.createCell(0).setCellValue("Name");
		row.createCell(1).setCellValue("Email ID");

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		wb.write(out);
		wb.close();

		return out.toByteArray();
	}

	public List<User> addUsers(MultipartFile file, Long id) throws Exception {
		Workbook workbook = new XSSFWorkbook(file.getInputStream());

		Sheet sheet = workbook.getSheetAt(0);
		List<User> users = new ArrayList<>();

		User contestCreator = userRepository.findUserById(id);

		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			User user = userRepository.findByEmail(sheet.getRow(i).getCell(1).toString());

			if (user == null) {
				User newUser = new User();
				newUser.setName(sheet.getRow(i).getCell(0).toString().toLowerCase());
				newUser.setEmail(sheet.getRow(i).getCell(1).toString());
				newUser.setPassword("NULL");
				newUser.setAvailable(false);

				Random r = new Random();
				int val = r.nextInt(1000000, 10000000);
				OTP otp = new OTP();
				otp.setEmail(sheet.getRow(i).getCell(1).toString().toLowerCase());
				otp.setOtp(val);
				userRepository.save(newUser);

				mailService.sendEmail(sheet.getRow(i).getCell(1).toString().toLowerCase(), "You are invited to a contest", "Dear user, \nYou are invited to a contest organized by " + contestCreator.getName() + "\n http://localhost:5173/first-time-user \nUse this link to create your password\n Your OTP is " + val);

				otpRepository.save(otp);
				users.add(newUser);
			} else {
				users.add(user);
			}
		}

		return users;
	}
}
