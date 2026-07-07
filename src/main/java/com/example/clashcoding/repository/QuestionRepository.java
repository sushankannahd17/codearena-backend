package com.example.clashcoding.repository;

import com.example.clashcoding.models.Question;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
	List<Question> findAllByUser_Id(Long userId);
}
