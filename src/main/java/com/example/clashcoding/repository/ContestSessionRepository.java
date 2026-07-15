package com.example.clashcoding.repository;

import com.example.clashcoding.models.Contest;
import com.example.clashcoding.models.ContestSession;
import com.example.clashcoding.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestSessionRepository extends JpaRepository<ContestSession, Long> {
	void deleteAllByContestId(Contest contestId);

	void deleteAllByContestId_Id(Long contestIdId);

	ContestSession findByContest_IdAndUser_Id(Long contestID, Long userID);
}
