ackage com.example.clashcoding.service;

import com.example.clashcoding.dto.ContestDTO;
import com.example.clashcoding.models.Contest;
import com.example.clashcoding.models.Question;
import com.example.clashcoding.models.User;
import com.example.clashcoding.repository.ContestRepository;
import com.example.clashcoding.repository.QuestionRepository;
import com.example.clashcoding.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
}