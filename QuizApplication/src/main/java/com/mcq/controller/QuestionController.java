package com.mcq.controller;

import com.mcq.entity.Questions;
import com.mcq.service.QuestionService;
import com.mcq.service.QuizService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "http://localhost:3000")
public class QuestionController {

    private final QuestionService questionService;
    private final QuizService quizService;

    // Inject both QuestionService and QuizService via the constructor
    public QuestionController(QuestionService questionService, QuizService quizService) {
        this.questionService = questionService;
        this.quizService = quizService;
    }

    @PostMapping("/addquestion")
    public Questions createQuestion(@RequestBody Questions question) {
        return questionService.createQuestion(question);
    }

    @GetMapping("/{id}")
    public Questions getQuestionById(@PathVariable Long id) {
        return questionService.getQuestionById(id);
    }

    @PutMapping("/{id}")
    public Questions updateQuestion(@PathVariable Long id, @RequestBody Questions question) {
        return questionService.updateQuestion(id, question);
    }

    @DeleteMapping("/{id}")
    public void deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
    }

    @GetMapping("/getAllQuestion")
    public List<Questions> getAllQuestions() {
        System.out.println("Inside Get all question");
        return questionService.getAllQuestions();
    }

    // This endpoint retrieves questions for a specific quiz.
    // It calls a method in QuizService to get questions based on a quiz ID.
    @GetMapping("/questions")
    public ResponseEntity<?> getQuestions(@RequestParam Long quizId) {
        try {
            List<Questions> questions = quizService.getQuestionsForQuiz(quizId);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error fetching questions: " + e.getMessage());
        }
    }
}
