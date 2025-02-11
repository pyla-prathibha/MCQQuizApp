package com.mcq.controller;

import com.mcq.dto.QuizSubmissionRequest;
import com.mcq.entity.Quiz;
import com.mcq.entity.QuizAttempt;
import com.mcq.entity.Users;
import com.mcq.repository.QuizAttemptRepository;
import com.mcq.repository.QuizRepository;
import com.mcq.repository.UserRepository;
import com.mcq.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = "http://localhost:3000")
public class QuizSubmissionController {

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizService quizService;

    @PostMapping("/submit-quiz")
    public ResponseEntity<?> submitQuiz(@RequestBody QuizSubmissionRequest submissionRequest) {
        Long quizId = submissionRequest.getQuizId();
        Long userId = submissionRequest.getUserId();
        List<com.mcq.dto.AnswerDTO> answers = submissionRequest.getAnswers();

        // 1. Check if the user has already taken this quiz.
        Optional<QuizAttempt> existingAttempt = quizAttemptRepository.findByQuiz_IdAndUser_Id(quizId, userId);
        if (existingAttempt.isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You have already taken this quiz. Your score: " + existingAttempt.get().getScore());
        }

        // 2. Process the quiz submission to calculate the score using the service.
        int score = quizService.calculateScore(quizId, answers);

        // 3. Create and save the quiz attempt.
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setUser(user);
        attempt.setScore(score);
        attempt.setAttemptTime(LocalDateTime.now());
        quizAttemptRepository.save(attempt);

        // 4. Return the result.
        return ResponseEntity.ok("Quiz submitted successfully. Your score: " + score);
    }

    @GetMapping("/attempt")
    public ResponseEntity<?> getQuizAttempt(@RequestParam Long quizId, @RequestParam Long userId) {
        Optional<QuizAttempt> attempt = quizAttemptRepository.findByQuiz_IdAndUser_Id(quizId, userId);
        if (attempt.isPresent()) {
            return ResponseEntity.ok(attempt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No attempt found");
        }
    }

}
