package com.example.codearena.repository;

import com.example.codearena.models.Contest;
import com.example.codearena.models.ContestSession;
import com.example.codearena.models.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestSessionRepository extends JpaRepository<ContestSession, Long> {
	void deleteAllByContestId(Contest contestId);

	void deleteAllByContestId_Id(Long contestIdId);

	ContestSession findByContest_IdAndUser_Id(Long contestID, Long userID);

	List<ContestSession> findAllByUser(User user);

	List<ContestSession> findAllByContest_IdAndUser_Id(Long contestId, Long userId);

	List<ContestSession> findAllByContest_Id(Long contestId);

	List<ContestSession> findAllByContest_IdOrderByQuestions(Long contestId, Sort sort);
}
