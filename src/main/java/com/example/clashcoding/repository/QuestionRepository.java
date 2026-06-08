package com.example.clashcoding.repository;

import com.example.clashcoding.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
