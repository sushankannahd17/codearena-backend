package com.example.clashcoding.repository;

import com.example.clashcoding.models.Contest;
import com.example.clashcoding.models.ContestSession;
import com.example.clashcoding.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestSessionRepository extends JpaRepository<ContestSession, Long> {
    ContestSession findByContestId_IdAndUserID_Id(Long contestIdId, Long userIDId);
}
