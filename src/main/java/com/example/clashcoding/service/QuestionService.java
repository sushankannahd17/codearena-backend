package com.example.clashcoding.service;

import java.util.List;

import com.example.clashcoding.config.ExceptionConfig;
import com.example.clashcoding.dto.ExamplesIODTO;
import com.example.clashcoding.dto.QuestionDTO;
import com.example.clashcoding.dto.QuestionResponseDTO;
import com.example.clashcoding.dto.TestCasesDTO;
import com.example.clashcoding.models.ExamplesIO;

import com.example.clashcoding.models.Question;
import com.example.clashcoding.models.TestCase;

import com.example.clashcoding.repository.QuestionRepository;
import com.example.clashcoding.repository.UserRepository;

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
        question.setUser(userRepository.findById(dto.getUserID()).orElseThrow(() -> new ExceptionConfig("User not found")));

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

        return questionRepository.save(question).getId();
    }

    public String modifyTestCases(Long questionID, List<TestCasesDTO> tcDTOList, Long userID) {
        Question question = questionRepository.findById(questionID).orElseThrow(() -> new ExceptionConfig("Question not found"));

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

    public String deleteQuestion(Long questionID) {
        Question question = questionRepository.findById(questionID).orElseThrow(() -> new ExceptionConfig("Question not found"));

        questionRepository.deleteById(questionID);

        return "Question deleted successfully";
    }

    public List<QuestionResponseDTO> getQuestions(Long id) {
        List<Question> questionList = questionRepository.findAllByUser_Id(id);

        return questionList.stream().map(q -> new QuestionResponseDTO(q.getId(), q.getTitle(), q.getDescription(), q.getExamples().stream().map(e -> new ExamplesIODTO(e.getInput(), e.getOutput(), e.getDescription())).toList(), q.getTc().stream().map(t -> new TestCasesDTO(t.getInput(), t.getOutput())).toList())).toList();
    }
}
