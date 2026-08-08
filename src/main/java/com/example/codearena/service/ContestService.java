package com.example.codearena.service;

import com.example.codearena.config.ExceptionConfig;
import com.example.codearena.dto.ContestDTO;
import com.example.codearena.models.*;
import com.example.codearena.repository.*;
import com.example.codearena.response.ContestEnterResponse;
import com.example.codearena.response.ExecuteResponse;
import com.example.codearena.response.ResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

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
		newContest.setMaxResumeCount(dto.getMaxResumeCount());

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

	@Transactional
	public String deleteContest(Long userID, Long contestId) {
		Contest contest = contestRepository.findById(contestId).orElseThrow();

		if (!contest.getUser().getId().equals(userID)) {
			throw new ExceptionConfig("User is not authorized");
		}

		submissionRepository.deleteAllByContest_Id(contest.getId());
		sessionRepository.deleteAllByContestId_Id(contest.getId());
		contestRepository.deleteById(contestId);
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
		Contest contest = contestRepository
			.findById(contestID).orElseThrow();

		boolean isAuthorized = contest.getAttenders().stream().anyMatch(user -> user.getId().equals(userID));

		if (!isAuthorized) {
			throw new ExceptionConfig("User is not authorized");
		}

		return contest;
	}

	public ContestEnterResponse validateAndEnter(Long contestID, Long userID) {
		Contest contest = contestRepository
			.findById(contestID).orElseThrow();

		boolean isAuthorized = contest.getAttenders().stream().anyMatch(user -> user.getId().equals(userID));

		if (!isAuthorized) {
			throw new ExceptionConfig("User is not authorized");
		}

		ContestSession contestSession = sessionRepository.findByContest_IdAndUser_Id(contest.getId(), userID);

		Instant now = Instant.now();
		if (now.isBefore(contest.getStartDate())) throw new ExceptionConfig("Contest is not started yet");

		if (now.isAfter(contest.getEndDate())) throw new ExceptionConfig("Contest is ended");

		if (contestSession == null) {
			ContestSession session = new ContestSession();
			session.setContest(contest);
			session.setUser(userRepository.findById(userID).orElseThrow());
			Instant endSession = now.plusSeconds(contest.getMinutes() * 60);
			Instant endContest = contest.getEndDate();
			session.setSessionEnd(endSession.isBefore(endContest) ? endSession : endContest);
			session.setQuestions(new ArrayList<>(contest.getQuestions()));
			session.setSubmitCount(0);
			session.setResumeCount(0);

			sessionRepository.save(session);

			return new ContestEnterResponse(
				contest.getQuestions(),
				session.getSessionEnd(),
				Duration.between(Instant.now(), session.getSessionEnd()).toSeconds(),
				0
			);
		}

		if (contestSession.getResumeCount() >= contest.getMaxResumeCount()) throw new ExceptionConfig(
			"You have exceeded the resume count, you cannot enter the contest"
		);

		if (contestSession.getSessionEnd().isBefore(Instant.now())) {
			throw new ExceptionConfig("Your attempt to attend the contest is over");
		}

		return new ContestEnterResponse(
			contestSession.getQuestions(),
			contestSession.getSessionEnd(),
			Math.max(0, Duration.between(Instant.now(), contestSession.getSessionEnd()).toSeconds()),
			contestSession.getResumeCount()
		);
	}

	public ResultResponse runCode(String code, Long userID, Long contestID, Long questionID, String language) throws Exception {
		Contest contest = contestRepository.findById(contestID).orElseThrow();

		Question question = questionRepository.findById(questionID).orElseThrow();
		List<ExamplesIO> examplesIOList = question.getExamples();
		List<TestCase> tcList = question.getTc();
		Long runTime = question.getRunTime();

		for (int i = 0; i < examplesIOList.size(); i++) {
			Future<ExecuteResponse> future = judgeService.executeAsync(code, language, examplesIOList.get(i).getInput(), question.getRunTime());
			ExecuteResponse response = future.get();

			if (response.getRun().getCode() == null || response.getRun().getCode() != 0) {

				return new ResultResponse("Error", response.getRun().getOutput());
			}

			String output = response.getRun().getOutput().trim().replaceAll("\\s+", " ");
			String expected = examplesIOList.get(i).getOutput().trim().replaceAll("\\s+", " ");

			if (!output.equals(expected)) {
				return new ResultResponse("Wrong answer", examplesIOList.get(i).getInput(), output, expected);
			}
		}

		return new ResultResponse("Sample Test Cases are passed successfully");
	}

	public ResultResponse submitCode(String code, Long userID, Long contestID, Long questionID, String language)
		throws Exception {
		Contest contest = contestRepository
			.findById(contestID).orElseThrow();
		Instant now = Instant.now();

		ContestSession session = sessionRepository.findByContest_IdAndUser_Id(contestID, userID);
		session.setSubmitCount(session.getSubmitCount() + 1);
		sessionRepository.save(session);

		if (now.isBefore(contest.getStartDate())) throw new ExceptionConfig("Contest has not started");
		if (now.isAfter(contest.getEndDate())) throw new ExceptionConfig("Contest has ended");

		Submission submission = submissionRepository.findByUser_IdAndContest_IdAndQuestion_Id(
			userID,
			contestID,
			questionID
		);

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
		List<TestCase> tcList = question.getTc();
		Long runTime = question.getRunTime();

		for (int i = 0; i < examplesIOList.size(); i++) {
			Future<ExecuteResponse> future = judgeService.executeAsync(code, language, examplesIOList.get(i).getInput(), question.getRunTime());
			ExecuteResponse response = future.get();
			if (response.getRun().getCode() == null || response.getRun().getCode() != 0) {

				return new ResultResponse("Error", response.getRun().getOutput());
			}

			String output = response.getRun().getOutput().trim().replaceAll("\\s+", " ");
			String expected = examplesIOList.get(i).getOutput().trim().replaceAll("\\s+", " ");

			if (!output.equals(expected)) {
				return new ResultResponse("Wrong answer", examplesIOList.get(i).getInput(), output, expected);
			}
		}

		for (int i = 0; i < tcList.size(); i++) {
			Future<ExecuteResponse> future = judgeService.executeAsync(code, language, tcList.get(i).getInput(), question.getRunTime());
			ExecuteResponse response = future.get();

			if (response.getRun().getCode() == null || response.getRun().getCode() != 0) {

				return new ResultResponse("Error", response.getRun().getOutput());
			}

			String output = response.getRun().getOutput().trim().replaceAll("\\s+", " ");
			String expected = tcList.get(i).getOutput().trim().replaceAll("\\s+", " ");

			if (!output.equals(expected)) {
				submission.setVerdict(
					(examplesIOList.size() + i) + " out of " + (tcList.size() + examplesIOList.size()) + " are correct"
				);
				submission.setPassedTC(examplesIOList.size() + i);
				submission.setTotalTC(examplesIOList.size() + tcList.size());
				submissionRepository.save(submission);
				return new ResultResponse(submission.getVerdict());
			}
		}

		submission.setVerdict("Accepted");
		submission.setPassedTC(examplesIOList.size() + tcList.size());
		submission.setTotalTC(examplesIOList.size() + tcList.size());
		submissionRepository.save(submission);

		session = sessionRepository.findByContest_IdAndUser_Id(contestID, userID);
		session.getQuestions().remove(question);

		if (session.getQuestions().size() == 0) {
			session.setSessionEnd(Instant.now());
		}

		sessionRepository.save(session);

		return new ResultResponse("Code Accepted");
	}

	public String fetchSubmittedCode(Long contestID, Long questionID, Long userID) {
		Submission oldSubmission = submissionRepository.findByUser_IdAndContest_IdAndQuestion_Id(
			userID,
			contestID,
			questionID
		);

		return (oldSubmission == null ? "" : oldSubmission.getCode());
	}

	public String modifyResumeCount(Long contestID, Long userID) {
		ContestSession session = sessionRepository.findByContest_IdAndUser_Id(contestID, userID);

		session.setResumeCount(session.getResumeCount() + 1);
		sessionRepository.save(session);

		return "Modified resume count";
	}
}