package com.example.codearena.repository;

import com.example.codearena.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);

	User findUserById(Long id);

	Iterable<User> findAllByIdNot(Long userID);
}
