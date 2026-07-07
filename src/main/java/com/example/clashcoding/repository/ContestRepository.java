package com.example.clashcoding.repository;

import com.example.clashcoding.models.Contest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestRepository extends JpaRepository<Contest, Long> {
	List<Contest> findContestByAttenders_Id(Long id);
	List<Contest> findContestByUser_Id(Long id);
}
