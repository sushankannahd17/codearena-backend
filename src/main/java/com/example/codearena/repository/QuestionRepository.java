package com.example.codearena.repository;

import com.example.codearena.models.Question;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
	List<Question> findAllByUser_Id(Long userId);

	Question findByIdAndUser_Id(Long questionID, Long userID);
}
