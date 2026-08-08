package com.example.codearena.service;

import com.example.codearena.config.ExceptionConfig;
import com.example.codearena.models.Contest;
import com.example.codearena.models.ContestSession;
import com.example.codearena.models.User;
import com.example.codearena.repository.ContestRepository;
import com.example.codearena.repository.ContestSessionRepository;
import com.example.codearena.repository.UserRepository;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeaderboardService {

	@Autowired
	private ContestSessionRepository repository;

	@Autowired
	private ContestRepository contestRepository;

	@Autowired
	private UserRepository userRepository;

	public List<ContestSession> getLeaderBoard(Long userID, Long contestID) {
		Contest contest = contestRepository.findById(contestID).orElseThrow();
		User user = userRepository.findById(userID).orElseThrow();

		if (!contest.getUser().getId().equals(user.getId())) {
			throw new ExceptionConfig("User is not authorized");
		}

		List<ContestSession> contestSessionList = repository.findAllByContest_Id(contestID);
		contestSessionList.sort(Comparator.comparingInt(s -> contest.getQuestions().size()));

		return contestSessionList;
	}

	public byte[] generateReport(Long contestID, Long userID) throws Exception {
		Contest contest = contestRepository.findById(contestID).orElseThrow();
		User user = userRepository.findById(userID).orElseThrow();

		if (!contest.getUser().getId().equals(user.getId())) {
			throw new ExceptionConfig("User is not authorized");
		}

		List<ContestSession> contestSessionList = repository.findAllByContest_Id(contestID);
		contestSessionList.sort(Comparator.comparingInt(s -> contest.getQuestions().size()));

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withZone(ZoneId.systemDefault());

		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("Leaderboard");

		int idx = 0;
		Row header = sheet.createRow(idx);
		header.createCell(0).setCellValue("Rank");
		header.createCell(1).setCellValue("Participant");
		header.createCell(2).setCellValue("Email ID");
		header.createCell(3).setCellValue("Total Questions Solved");
		header.createCell(4).setCellValue("Submit Count");
		header.createCell(5).setCellValue("Duration");
		header.createCell(6).setCellValue("Start Time");
		header.createCell(7).setCellValue("End Time");
		header.createCell(8).setCellValue("Status");
		header.createCell(9).setCellValue("Resume Count");

		idx++;
		for (int i = 0; i < contestSessionList.size(); i++) {
			Row row = sheet.createRow(idx);
			row.createCell(0).setCellValue(idx);
			row.createCell(1).setCellValue(contestSessionList.get(i).getUser().getName());
			row.createCell(2).setCellValue(contestSessionList.get(i).getUser().getEmail());
			row
				.createCell(3)
				.setCellValue(contest.getQuestions().size() - contestSessionList.get(i).getQuestions().size());
			row.createCell(4).setCellValue(contestSessionList.get(i).getSubmitCount());
			Instant startDate = contestSessionList.get(i).getSessionStart();
			Instant endDate = contestSessionList.get(i).getSessionEnd();
			long seconds = Duration.between(startDate, endDate).getSeconds();
			long hours = seconds / 3600;
			seconds %= 3600;
			long minutes = seconds / 60;
			seconds %= 60;
			row.createCell(5).setCellValue(hours + " hours " + minutes + " minutes " + seconds + " seconds");
			row.createCell(6).setCellValue(formatter.format(startDate));
			row.createCell(7).setCellValue(formatter.format(endDate));
			row
				.createCell(8)
				.setCellValue((contestSessionList.get(i).getQuestions().size() == 0) ? "Completed" : "Not Completed");
			row.createCell(9).setCellValue(contestSessionList.get(i).getResumeCount());
			idx++;
		}

		for (int i=0; i <=9; i++) sheet.autoSizeColumn(i);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		wb.write(out);
		wb.close();

		return out.toByteArray();
	}
}
