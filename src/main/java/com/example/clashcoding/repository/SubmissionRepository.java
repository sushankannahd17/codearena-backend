package com.example.clashcoding.repository;

import com.example.clashcoding.models.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Submission findByUser_IdAndContest_IdAndQuestion_Id(Long userID, Long contestID, Long questionID);
}
