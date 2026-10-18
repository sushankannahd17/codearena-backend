package com.example.codearena.repository;

import com.example.codearena.models.OTP;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OTPRepository extends JpaRepository<OTP, Long> {
	OTP findAllByEmail(String email);

	void deleteAllByEmail(String email);
}
