package com.example.codearena.service;

import com.example.codearena.models.*;
import com.example.codearena.repository.ContestRepository;
import com.example.codearena.repository.ContestSessionRepository;
import com.example.codearena.repository.SubmissionRepository;
import com.example.codearena.repository.UserRepository;
import com.example.codearena.response.ReportResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    public List<ReportResponse> displayCompletedContests(Long userID) {
        Instant now = Instant.now();
        User loggedInUser = userRepository.findUserById(userID);

        List<Contest> contestList = contestRepository.findByAttendersAndEndDateAfter(loggedInUser, now);
        List<ReportResponse> reportList = new ArrayList<>();

        for (Contest contest : contestList) {
            ReportResponse report = new ReportResponse();
            report.setId(contest.getId());
            report.setContestname(contest.getContestname());
            report.setStartDate(contest.getStartDate());
            report.setEndDate(contest.getEndDate());
            report.setMinutes(contest.getMinutes());
            report.setMaxResumeCount(contest.getMaxResumeCount());
            report.setQuestions(contest.getQuestions());
            List<Submission> submissions = new ArrayList<>();
            ContestSession session = sessionRepository.findByContest_IdAndUser_Id(contest.getId(), userID);
            if (session != null) {
                report.setUserStartDate(session.getSessionStart());
                report.setUserEndDate(session.getSessionEnd());
                report.setSubmitCount(session.getSubmitCount());
                report.setResumeCount(session.getResumeCount());
            }

            for (Question question : contest.getQuestions()) {
                Submission submission = submissionRepository.findByUser_IdAndContest_IdAndQuestion_Id(userID, contest.getId(), question.getId());
                if(submission != null){
                    submissions.add(submission);
                }
            }
            report.setSubmissions(submissions);

            reportList.add(report);
        }

        return reportList;
    }
}