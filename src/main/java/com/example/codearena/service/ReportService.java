package com.example.codearena.service;

import com.example.codearena.models.*;
import com.example.codearena.repository.ContestRepository;
import com.example.codearena.repository.ContestSessionRepository;
import com.example.codearena.repository.SubmissionRepository;
import com.example.codearena.repository.UserRepository;
import com.example.codearena.response.ReportResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.example.codearena.utils.WordUtils;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

	@Autowired
	private ContestRepository contestRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SubmissionRepository submissionRepository;

	@Autowired
	private ContestSessionRepository sessionRepository;

	@Autowired
	private WordUtils wordUtils;

	public List<ReportResponse> displayCompletedContests(Long userID) {

		Instant now = Instant.now();

		User loggedInUser = userRepository.findUserById(userID);

		List<Contest> contestList =
				contestRepository.findByAttenders(loggedInUser);

		List<ReportResponse> reportList = new ArrayList<>();

		for (Contest contest : contestList) {

			ReportResponse report = new ReportResponse();

			report.setId(contest.getId());
			report.setContestname(contest.getContestname());

			if (contest.getEndDate().isAfter(now)) {
				report.setContestOver(false);
				reportList.add(report);
				continue;
			}

			report.setContestOver(true);
			report.setStartDate(contest.getStartDate());
			report.setEndDate(contest.getEndDate());
			report.setMinutes(contest.getMinutes());
			report.setMaxResumeCount(contest.getMaxResumeCount());
			report.setQuestions(contest.getQuestions());

			ContestSession session =
					sessionRepository.findByContest_IdAndUser_Id(
							contest.getId(),
							userID
					);

			if (session != null) {

				report.setUserStartDate(session.getSessionStart());
				report.setUserEndDate(session.getSessionEnd());
				report.setSubmitCount(session.getSubmitCount());
				report.setResumeCount(session.getResumeCount());

				report.setCompleted(session.isCompleted());

				if (session.getQuestions() != null) {
					report.setSolvedCount(
							contest.getQuestions().size()
									- session.getQuestions().size()
					);
				} else {
					report.setSolvedCount(0);
				}

			} else {

				report.setCompleted(false);
				report.setSolvedCount(0);
			}

			List<Submission> submissions = new ArrayList<>();

			for (Question question : contest.getQuestions()) {

				Submission submission =
						submissionRepository
								.findByUser_IdAndContest_IdAndQuestion_Id(
										userID,
										contest.getId(),
										question.getId()
								);

				if (submission != null) {
					submissions.add(submission);
				}
			}

			report.setSubmissions(submissions);

			reportList.add(report);
		}

		return reportList;
	}

	public byte[] generateReport(Long userID, Long contestID) throws IOException {
		ContestSession session = sessionRepository.findByContest_IdAndUser_Id(contestID, userID);
		List<Submission> submissions = submissionRepository.findAllByContest_IdAndUser_Id(contestID, userID);

		XWPFDocument document = new XWPFDocument();
		wordUtils.addHeading(document, "CONTEST REPORT - " + session.getContest().getContestname().toUpperCase());

		wordUtils.addSubHeading(document, "CONTEST DETAILS");
		wordUtils.addLabelValue(document, "Contest Name", session.getContest().getContestname(), false);
		wordUtils.addLabelValue(document, "Contest Start Date", LocalDateTime
				.ofInstant(session.getContest().getStartDate(), ZoneId.systemDefault())
				.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")), false);
		wordUtils.addLabelValue(document, "Contest End Date", LocalDateTime
				.ofInstant(session.getContest().getEndDate(), ZoneId.systemDefault())
				.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")), false);
		wordUtils.addLabelValue(document, "User Session Start Date", LocalDateTime
				.ofInstant(session.getSessionStart(), ZoneId.systemDefault())
				.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")), false);
		wordUtils.addLabelValue(document, "User Session End Date", LocalDateTime
				.ofInstant(session.getSessionEnd(), ZoneId.systemDefault())
				.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")), false);
		wordUtils.addLabelValue(document, "Resume Count", session.getResumeCount() + " / " + session.getContest().getMaxResumeCount(), false);
		Instant startDate = session.getSessionStart();
		Instant endDate = session.getSessionEnd();
		long seconds = Duration.between(startDate, endDate).getSeconds();
		long hours = seconds / 3600;
		seconds %= 3600;
		long minutes = seconds / 60;
		seconds %= 60;
		wordUtils.addLabelValue(document, "Session Duration", hours + " hours " + minutes + " minutes " + seconds + " seconds", false);
		wordUtils.addLabelValue(document, "Status", session.isCompleted()?"Completed":"Not Completed", false);
		wordUtils.addLabelValue(document, "Submission Count", String.valueOf(session.getSubmitCount()), false);
		wordUtils.addLabelValue(document, "Solved Count", String.valueOf(session.getContest().getQuestions().size() - session.getQuestions().size()), false);

		for (int i=0; i<session.getContest().getQuestions().size(); i++) {
			Question question = session.getContest().getQuestions().get(i);

			Submission submission = submissions.stream()
					.filter(s -> s.getQuestion().getId().equals(question.getId()))
					.findFirst()
					.orElse(null);

			wordUtils.addLabelValue(document, "Question " + (i + 1), question.getTitle(), false);
			wordUtils.addSubHeading(document, "Description");
			wordUtils.addText(document, question.getDescription());

			for (int idx = 0; idx < question.getExamples().size(); idx++) {
				wordUtils.addSubHeading(document, "Example TestCase " + (idx + 1));
				wordUtils.addSubHeading(document, "Input");
				wordUtils.addText(document, question.getExamples().get(idx).getInput());
				wordUtils.addSubHeading(document, "Output");
				wordUtils.addText(document, question.getExamples().get(idx).getOutput());
			}
			wordUtils.addText(document, "Maximum Execution Time Limit: " + question.getRunTime() + " millisecs");
			if (submission == null) {
				wordUtils.addLabelValue(document, "Verdict", "Question Not Attempted", false);
			} else {
				wordUtils.addLabelValue(document,"Verdict", submission.getVerdict(), false);
				wordUtils.addLabelValue(document,"Passed Testcases", String.valueOf(submission.getPassedTC()), false);
				wordUtils.addLabelValue(document,"Total Testcases", String.valueOf(submission.getTotalTC()), false);
				wordUtils.addSubHeading(document, "CODE");
				wordUtils.addCode(document, submission.getCode());
			}
		}

		ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
		document.write(outputStream);
		document.close();

		return outputStream.toByteArray();
	}
}
