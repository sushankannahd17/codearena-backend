package com.example.clashcoding.repository;

import com.example.clashcoding.models.ExamplesIO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExampleIORepository extends JpaRepository<ExamplesIO, Long> {
    ExamplesIO findByProblem_Id(Long problemId);
}
