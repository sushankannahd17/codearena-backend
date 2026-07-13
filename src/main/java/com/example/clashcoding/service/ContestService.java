package com.example.clashcoding.service;

import com.example.clashcoding.config.ExceptionConfig;
import com.example.clashcoding.dto.ContestDTO;
import com.example.clashcoding.models.*;
import com.example.clashcoding.repository.*;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.clashcoding.response.ContestEnterResponse;
import com.example.clashcoding.response.ExecuteResponse;
import com.example.clashcoding.response.ResultResponse;
import com.example.clashcoding.response.RunResponse;
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

	@Autowired
	private ContestSessionRepository sessionRepository;
    @Autowired
    private SubmissionRepository submissionRepository;
	@Autowired
	private JudgeService judgeService;

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

	public Contest getContest(Long contestID, Long userID) {
		Contest contest = contestRepository.findById(contestID)
				.orElseThrow(() -> new ExceptionConfig("Contest not found"));

		boolean isAuthorized = contest.getAttenders().stream().anyMatch(user -> user.getId().equals(userID));

		if (!isAuthorized) {
			throw new ExceptionConfig("User is not authorized");
		}

		return contest;
	}

    public ContestEnterResponse validateAndEnter(Long contestID, Long userID) {
        Contest contest = contestRepository.findById(contestID).orElseThrow(() -> new ExceptionConfig("Contest not found"));

		boolean isAuthorized = contest.getAttenders().stream().anyMatch(user -> user.getId().equals(userID));

		if (!isAuthorized) {
			throw new ExceptionConfig("User is not authorized");
		}

		ContestSession contestSession = sessionRepository.findByContestId_IdAndUserID_Id(contest.getId(), userID);

		Instant now = Instant.now();
		if (now.isBefore(contest.getStartDate())) throw new ExceptionConfig("Contest is not started yet");

		if (now.isAfter(contest.getEndDate())) throw new ExceptionConfig("Contest is ended");

		if (contestSession == null) {
			ContestSession session = new ContestSession();
			session.setContestId(contest);
			session.setUserID(userRepository.findById(userID).orElseThrow());
			Instant endSession = now.plusSeconds(contest.getMinutes() * 60);
			Instant endContest = contest.getEndDate();
			session.setSessionEnd(endSession.isBefore(endContest) ? endSession : endContest);
			session.setQuestions(new ArrayList<>(contest.getQuestions()));
			session.setStatus("Ongoing");

			sessionRepository.save(session);

			return new ContestEnterResponse(contest.getQuestions(), session.getSessionEnd(), Duration.between(Instant.now(), session.getSessionEnd()).toSeconds(), session.getStatus());
		}

		return new ContestEnterResponse(contestSession.getQuestions(), contestSession.getSessionEnd(), Math.max(0, Duration.between(Instant.now(), contestSession.getSessionEnd()).toSeconds()), contestSession.getStatus());
    }

	public ResultResponse submitCode(String code, Long userID, Long contestID, Long questionID, String language) throws Exception {
		Contest contest = contestRepository.findById(contestID).orElseThrow(() -> new ExceptionConfig("Contest not found"));
		Instant now = Instant.now();

		if (now.isBefore(contest.getStartDate())) throw new ExceptionConfig("Contest has not started");
		if (now.isAfter(contest.getEndDate())) throw  new ExceptionConfig("Contest has ended");

		Submission submission = submissionRepository.findByUser_IdAndContest_IdAndQuestion_Id(userID, contestID, questionID);

		if (submission == null) {
			Submission newSubmission = new Submission();
			newSubmission.setUser(userRepository.findById(userID).orElseThrow());
			newSubmission.setContest(contestRepository.findById(contestID).orElseThrow());
			newSubmission.setQuestion(questionRepository.findById(questionID).orElseThrow());
			newSubmission.setCode(code);
			newSubmission.setLanguage(language);

			submissionRepository.save(newSubmission);
			submission = newSubmission;
		} else {
			submission.setCode(code);
			submission.setLanguage(language);
			submissionRepository.save(submission);
		}

		Question question = questionRepository.findById(questionID).orElseThrow();
		List<ExamplesIO> examplesIOList = question.getExamples();

		for (int i = 0; i < examplesIOList.size(); i++) {
			ExecuteResponse response = judgeService.execute(code, language, examplesIOList.get(i).getInput());

			if (response.getRun().getCode() != 0) {
				submission.setVerdict("Runtime Error");
				submissionRepository.save(submission);
				return new ResultResponse(submission.getVerdict(), response.getRun().getStderr());
			}

			String output = response.getRun().getOutput().trim();
			String expected = examplesIOList.get(i).getOutput().trim();

			if (!output.equals(expected)) {
				submission.setVerdict("Wrong Answer");
				submissionRepository.save(submission);
				return new ResultResponse(submission.getVerdict(), examplesIOList.get(i).getInput(), output, expected);
			}
		}

		List<TestCase> tcList = question.getTc();

		for (int i = 0; i < tcList.size(); i++) {
			ExecuteResponse response = judgeService.execute(code, language, tcList.get(i).getInput());

			if (response.getRun().getCode() != 0) {
				submission.setVerdict("Runtime Error");
				submissionRepository.save(submission);
				return new ResultResponse(submission.getVerdict());
			}

			String output = response.getRun().getOutput().trim();
			String expected = tcList.get(i).getOutput().trim();

			if (!output.equals(expected)) {
				submission.setVerdict((i) + " out of " + tcList.size() + " are correct");
				submissionRepository.save(submission);
				return new ResultResponse(submission.getVerdict());
			}
		}

		submission.setVerdict("Accepted");
		submissionRepository.save(submission);

		ContestSession session = sessionRepository.findByContestId_IdAndUserID_Id(contestID, userID);
		session.getQuestions().remove(question);

		if (session.getQuestions().size() == 0) {
			session.setStatus("Completed");
		}

		sessionRepository.save(session);

		return new ResultResponse("Code Accepted");
	}
}
