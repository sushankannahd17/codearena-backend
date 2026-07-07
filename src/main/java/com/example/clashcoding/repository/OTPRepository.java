package com.example.clashcoding.repository;

import com.example.clashcoding.models.OTP;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OTPRepository extends JpaRepository<OTP, Long> {
	OTP findAllByEmail(String email);

	void deleteAllByEmail(String email);
}
