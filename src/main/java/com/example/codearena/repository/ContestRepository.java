package com.example.codearena.repository;

import com.example.codearena.models.Contest;

import java.time.Instant;
import java.util.List;

import com.example.codearena.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestRepository extends JpaRepository<Contest, Long> {
	List<Contest> findContestByAttenders_Id(Long id);
	List<Contest> findContestByUser_Id(Long id);

	List<Contest> findByUser_Id(Long userId);

	List<Contest> findByAttendersAndEndDateBefore(User attenders, Instant endDateAfter);
	List<Contest> findByAttendersAndEndDateAfter(User attenders, Instant endDateAfter);
}
