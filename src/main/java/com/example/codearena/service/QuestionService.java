package com.example.codearena.service;

import com.example.codearena.config.ExceptionConfig;
import com.example.codearena.dto.ExamplesIODTO;
import com.example.codearena.dto.QuestionDTO;
import com.example.codearena.dto.QuestionResponseDTO;
import com.example.codearena.dto.TestCasesDTO;
import com.example.codearena.models.ExamplesIO;
import com.example.codearena.models.Question;
import com.example.codearena.models.TestCase;
import com.example.codearena.repository.QuestionRepository;
import com.example.codearena.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private QuestionRepository questionRepository;

	public Long createQuestion(QuestionDTO dto) {
		Question question = new Question();
		question.setTitle(dto.getTitle());
		question.setDescription(dto.getDescription());
		question.setUser(
			userRepository.findById(dto.getUserID()).orElseThrow()
		);

		for (ExamplesIODTO exampleDTO : dto.getExamplesIO()) {
			ExamplesIO examplesIO = new ExamplesIO();
			examplesIO.setInput(exampleDTO.getInput());
			examplesIO.setOutput(exampleDTO.getOutput());
			examplesIO.setDescription(exampleDTO.getDescription());
			examplesIO.setQuestion(question);

			question.getExamples().add(examplesIO);
		}

		for (TestCasesDTO tcDTO : dto.getTestcases()) {
			TestCase tc = new TestCase();
			tc.setInput(tcDTO.getInput());
			tc.setOutput(tcDTO.getOutput());
			tc.setQuestion(question);

			question.getTc().add(tc);
		}
		question.setRunTime(dto.getRunTime());

		return questionRepository.save(question).getId();
	}

	public String modifyQuestion(QuestionDTO dto, Long questionID) {
		Question question = questionRepository.findById(questionID).orElseThrow();
		question.setTitle(dto.getTitle());
		question.setDescription(dto.getDescription());
		question.setUser(
				userRepository.findById(dto.getUserID()).orElseThrow()
		);

		for (ExamplesIODTO exampleDTO : dto.getExamplesIO()) {
			ExamplesIO examplesIO = new ExamplesIO();
			examplesIO.setInput(exampleDTO.getInput());
			examplesIO.setOutput(exampleDTO.getOutput());
			examplesIO.setDescription(exampleDTO.getDescription());
			examplesIO.setQuestion(question);

			question.getExamples().add(examplesIO);
		}

		for (TestCasesDTO tcDTO : dto.getTestcases()) {
			TestCase tc = new TestCase();
			tc.setInput(tcDTO.getInput());
			tc.setOutput(tcDTO.getOutput());
			tc.setQuestion(question);

			question.getTc().add(tc);
		}
		question.setRunTime(dto.getRunTime());

		questionRepository.save(question);

		return "Question modified successfully";
	}

	public String modifyTestCases(Long questionID, List<TestCasesDTO> tcDTOList, Long userID) {
		Question question = questionRepository
			.findById(questionID)
			.orElseThrow(() -> new ExceptionConfig("Question not found"));

		if (!(question.getUser().getId().equals(userID))) {
			throw new ExceptionConfig("User is not authorized");
		}

		question.getTc().clear();

		for (TestCasesDTO tcDTO : tcDTOList) {
			TestCase tc = new TestCase();
			tc.setInput(tcDTO.getInput());
			tc.setOutput(tcDTO.getOutput());
			tc.setQuestion(question);

			question.getTc().add(tc);
		}

		questionRepository.save(question);
		return "TestCases Updated Successfully";
	}

	public String deleteQuestion(Long questionID, Long userID) {
		Question question = questionRepository
			.findById(questionID)
			.orElseThrow(() -> new ExceptionConfig("Question not found"));

		if (!(question.getUser().getId().equals(userID))) throw new ExceptionConfig("User is not authorized");

		questionRepository.deleteById(questionID);

		return "Question deleted successfully";
	}

	public List<QuestionResponseDTO> getQuestions(Long id) {
		List<Question> questionList = questionRepository.findAllByUser_Id(id);

		return questionList
			.stream()
			.map(q ->
				new QuestionResponseDTO(
					q.getId(),
					q.getTitle(),
					q.getDescription(),
					q
						.getExamples()
						.stream()
						.map(e -> new ExamplesIODTO(e.getInput(), e.getOutput(), e.getDescription()))
						.toList(),
					q.getTc().stream().map(t -> new TestCasesDTO(t.getInput(), t.getOutput())).toList(),
						q.getRunTime()
				)
			)
			.toList();
	}
}
