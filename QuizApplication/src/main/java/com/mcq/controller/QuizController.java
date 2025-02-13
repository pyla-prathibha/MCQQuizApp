package com.mcq.controller;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mcq.dto.InviteRequest;
import com.mcq.entity.Questions;
import com.mcq.entity.Quiz;
import com.mcq.repository.QuizRepository;
import com.mcq.repository.QuizInviteRepository;
import com.mcq.service.QuizService;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = "http://localhost:3000")
public class QuizController {

	private final QuizService quizService;
	private final QuizRepository quizRepository;

	@Autowired
	private QuizInviteRepository quizInviteRepository;

	public QuizController(QuizService quizService, QuizRepository quizRepository) {
		this.quizService = quizService;
		this.quizRepository = quizRepository;
	}

	@PostMapping
	public ResponseEntity<?> createQuiz(@RequestBody Map<String, Object> requestData) {
		try {
			// Extract data from the request
			String quizName = (String) requestData.get("quizName");
			String technology = (String) requestData.get("technology");
			String startTime = (String) requestData.get("startTime");
			String endTime = (String) requestData.get("endTime");
			int numQuestions = (int) requestData.get("numQuestions");
			List<Integer> invitedUserIds = (List<Integer>) requestData.getOrDefault("invitedUsers", new ArrayList<>());

			// Convert invitedUserIds to List<Long>
			List<Long> invitedUsers = invitedUserIds.stream()
					.map(Long::valueOf)
					.collect(Collectors.toList());

			Quiz quiz = new Quiz();
			quiz.setQuizName(quizName);
			quiz.setTechnology(technology);
			quiz.setStartTime(LocalDateTime.parse(startTime));
			quiz.setEndTime(LocalDateTime.parse(endTime));

			Quiz savedQuiz = quizService.createQuiz(quiz, numQuestions, invitedUsers);

			return ResponseEntity.ok(savedQuiz);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Error creating quiz: " + e.getMessage());
		}
	}

	// Endpoint to invite users to a quiz.
	// The request body should be a JSON object with keys "quizId" and "invitedUsers".
	// Example:
	// {
	//   "quizId": 1,
	//   "invitedUsers": [7]
	// }
	@PostMapping("/invite-users")
	public ResponseEntity<?> inviteUsers(@RequestBody InviteRequest inviteRequest) {
		// Log or inspect the inviteRequest object to confirm the fields
		System.out.println("Received quizId: " + inviteRequest.getQuizId());
		System.out.println("Received invitedUserNumbers: " + inviteRequest.getInvitedUserNumbers());

		// Process the invitation. (This example just logs the user IDs.)
		inviteRequest.getInvitedUserNumbers().stream()
				.forEach(userId -> System.out.println("Inviting user: " + userId));

		return ResponseEntity.ok("Users invited successfully");
	}
	@GetMapping("/isUserInvited/{quizId}")
	public ResponseEntity<Map<String, String>> isUserInvited(
			@PathVariable Long quizId,
			@RequestParam Long userId) {

		boolean isInvited = quizInviteRepository.existsByQuiz_IdAndUser_IdAndInvitedStatus(quizId, userId, true);
		System.out.println("Backend Check: quizId=" + quizId + ", userId=" + userId + ", isInvited=" + isInvited);

		Map<String, String> response = new HashMap<>();
		response.put("isInvited", isInvited ? "true" : "false");

		if (!isInvited) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(Collections.singletonMap("error", "User is not invited to this quiz"));
		}

		return ResponseEntity.ok(response);
	}


	@GetMapping("/{id}")
	public Quiz getQuizById(@PathVariable Long id) {
		return quizService.getQuizById(id);
	}

	@PutMapping("/{id}")
	public Quiz updateQuiz(@PathVariable Long id, @RequestBody Quiz quiz) {
		return quizService.updateQuiz(id, quiz);
	}

	@DeleteMapping("/{id}")
	public void deleteQuiz(@PathVariable Long id) {
		quizService.deleteQuiz(id);
	}

	@GetMapping
	public List<Quiz> getAllQuizzes() {
		return quizService.getAllQuizzes();
	}

	@GetMapping("/getQuizQuestById/{id}")
	public List<Questions> getQuizQuestById(@PathVariable Long id) {
		return quizService.getQuizQuestById(id);
	}

	@GetMapping("/getQuizTime/{quizId}")
	public ResponseEntity<Map<String, String>> getQuizTime(@PathVariable Long quizId) {
		Optional<Quiz> quiz = quizRepository.findById(quizId);
		if (quiz.isPresent()) {
			Map<String, String> response = new HashMap<>();
			response.put("startTime", quiz.get().getStartTime().toString());
			response.put("endTime", quiz.get().getEndTime().toString());
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}
}
