package com.example.clashcoding.repository;

import com.example.clashcoding.models.Question;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByUser_Id(Long userId);
}
