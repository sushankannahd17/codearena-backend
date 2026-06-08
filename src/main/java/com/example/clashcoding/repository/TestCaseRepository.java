package com.example.clashcoding.repository;

import com.example.clashcoding.models.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    TestCase findByProblem_Id(Long problemId);
}
