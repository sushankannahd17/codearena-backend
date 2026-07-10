package com.example.clashcoding.service;

import com.example.clashcoding.config.ExceptionConfig;
import com.example.clashcoding.dto.ContestDTO;
import com.example.clashcoding.models.Contest;
import com.example.clashcoding.models.Question;
import com.example.clashcoding.models.User;
import com.example.clashcoding.repository.ContestRepository;
import com.example.clashcoding.repository.QuestionRepository;
import com.example.clashcoding.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContestService {

	@Autowired
	private ContestRepository contestRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private QuestionRepository questionRepository;

	public String createContest(ContestDTO dto, Long userID) {
		Contest newContest = new Contest();
		List<Question> questionList = questionRepository.findAllById(dto.getQuestionID());
		List<User> attenderList = userRepository.findAllById(dto.getAttenderID());
		User creator = userRepository.findById(userID).orElseThrow();

		newContest.setContestname(dto.getName());
		newContest.setAttenders(attenderList);
		newContest.setQuestions(questionList);
		newContest.setMinutes(dto.getMinutes());
		newContest.setUser(creator);
		newContest.setStartDate(dto.getStartDate());
		newContest.setEndDate(dto.getEndDate());

		contestRepository.save(newContest);
		return "Contest has been created successfully";
	}

	public List<Contest> getContests(Long userID) {
		List<Contest> contests = contestRepository.findContestByUser_Id(userID);

		return contests;
	}

	public List<Contest> viewAvailableContests(Long userID) {
		List<Contest> contests = contestRepository.findContestByAttenders_Id(userID);

		return contests;
	}

	public String deleteContest(Long userID, Long questionID) {
		Contest contest = contestRepository.findById(questionID).orElseThrow();

		if (!contest.getUser().getId().equals(userID)) {
			throw new ExceptionConfig("User is not authorized");
		}

		contestRepository.deleteById(questionID);
		return "Contest deleted successfully";
	}

	public String modifyQuestions(Long contestID, Long userID, ContestDTO dto) {
		Contest contest = contestRepository.findById(contestID).orElseThrow();

		if (!contest.getUser().getId().equals(userID)) throw new ExceptionConfig("User is not authorized");

		contest.getQuestions().clear();
		contest.getAttenders().clear();
		for (Long id : dto.getQuestionID()) {
			Question question = questionRepository.findById(id).orElseThrow();
			contest.getQuestions().add(question);
		}

		for (Long id : dto.getAttenderID()) {
			User user = userRepository.findById(id).orElseThrow();
			contest.getAttenders().add(user);
		}

		contestRepository.save(contest);
		return "Contest modified successfully";
	}
}
