package com.mcq.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import com.mcq.dto.AnswerDTO;
import com.mcq.entity.Quiz;
import com.mcq.entity.QuizAttempt;
import com.mcq.entity.QuizQuestion;
import com.mcq.entity.Questions;
import com.mcq.entity.Users;
import com.mcq.repository.QuizAttemptRepository;
import com.mcq.repository.QuizQuestionRepository;
import com.mcq.repository.QuizRepository;
import com.mcq.repository.QuestionRepository;
import com.mcq.repository.QuizInviteRepository;
import com.mcq.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

@Service
public class QuizService {

	@Autowired
	private QuizRepository quizRepository;

	@Autowired
	private QuizQuestionRepository quizQuestionRepository;

	@Autowired
	private QuestionRepository questRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private QuizInviteRepository quizInviteRepository;

	// Enhanced createQuiz method
	public Quiz createQuiz(Quiz quiz, int numQuestions, List<Long> invitedUserIds) {
		List<Users> invitedUsers = userRepository.findAllById(invitedUserIds);
		if (invitedUsers.isEmpty()) {
			throw new IllegalArgumentException("No users were invited.");
		}

		// Fetch all available questions from the repository
		List<Questions> allQuestions = questRepository.findAll();
		if (allQuestions.size() < numQuestions) {
			throw new IllegalArgumentException("Not enough questions available to create the quiz.");
		}

		// Randomly select questions for the quiz
		List<Questions> selectedQuestions = new ArrayList<>();
		Random random = new Random();
		while (selectedQuestions.size() < numQuestions) {
			Questions question = allQuestions.get(random.nextInt(allQuestions.size()));
			if (!selectedQuestions.contains(question)) {
				selectedQuestions.add(question);
			}
		}

		// Convert selected questions into QuizQuestion entities
		List<QuizQuestion> quizQuestions = new ArrayList<>();
		for (Questions question : selectedQuestions) {
			QuizQuestion quizQuestion = new QuizQuestion();
			quizQuestion.setQuiz(quiz);
			quizQuestion.setQuestion(question);
			quizQuestions.add(quizQuestion);
		}
		quiz.setQuizQuestions(quizQuestions);

		// Save the quiz first so that its generated ID is available.
		Quiz savedQuiz = quizRepository.save(quiz);

		// Save invited users in quiz_invites via the join entity
		List<QuizAttempt> dummy = new ArrayList<>(); // Not used here, but you might save invites elsewhere
		List<?> invites = new ArrayList<>();
		for (Users user : invitedUsers) {
			// Create and save a QuizInvite entity for each invited user.
			// (Your invitation code already exists elsewhere; see your inviteUsersToQuiz method.)
			// Here we assume your service method handles invitations.
		}
		quizInviteRepository.saveAll(
				invitedUsers.stream().map(user -> {
					com.mcq.entity.QuizInvite invite = new com.mcq.entity.QuizInvite();
					invite.setQuiz(savedQuiz);
					invite.setUser(user);
					invite.setInvitedStatus(true);
					return invite;
				}).collect(Collectors.toList())
		);

		return savedQuiz;
	}

	// Get quiz time by ID using a custom query from quizRepository
	public Quiz getQuizTime(Long id) {
		return quizRepository.findQuizTimeById(id);
	}

	// Get a quiz by ID (single definition)
	public Quiz getQuizById(Long id) {
		return quizRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Quiz not found"));
	}

	// Update quiz details
	public Quiz updateQuiz(Long id, Quiz updatedQuiz) {
		Quiz quiz = getQuizById(id);
		quiz.setQuizName(updatedQuiz.getQuizName());
		quiz.setTechnology(updatedQuiz.getTechnology());
		return quizRepository.save(quiz);
	}

	// Delete quiz by ID
	public void deleteQuiz(Long id) {
		Quiz quiz = getQuizById(id);
		quizRepository.delete(quiz);
	}

	// Get all quizzes
	public List<Quiz> getAllQuizzes() {
		return quizRepository.findAll();
	}

	// Get quiz questions by quiz ID using the QuizQuestion join entity
	public List<Questions> getQuizQuestById(Long id) {
		List<QuizQuestion> quizQuesList = quizQuestionRepository.findByQuiz(getQuizById(id));
		List<Questions> questionList = new ArrayList<>();
		for (QuizQuestion quizQues : quizQuesList) {
			Optional<Questions> optionalQuestions = questRepository.findById(quizQues.getQuestion().getId());
			optionalQuestions.ifPresent(questionList::add);
		}
		return questionList;
	}

	// Method to check if the user is invited to a quiz using the join table directly
	public boolean isUserInvited(Long quizId, Long userId) {
		return quizInviteRepository.existsByQuiz_IdAndUser_IdAndInvitedStatus(quizId, userId, true);
	}

	// Calculate score using the quiz's questions.
	public int calculateScore(Long quizId, List<AnswerDTO> answers) {
		// First, fetch the Quiz entity
		Quiz quiz = quizRepository.findById(quizId)
				.orElseThrow(() -> new RuntimeException("Quiz not found"));
		// Then retrieve the quiz questions using the join entity:
		List<QuizQuestion> quizQuestions = quizQuestionRepository.findByQuiz(quiz);
		List<Questions> questions = quizQuestions.stream()
				.map(QuizQuestion::getQuestion)
				.collect(Collectors.toList());
		int score = 0;
		for (AnswerDTO answer : answers) {
			Questions question = questions.stream()
					.filter(q -> q.getId().equals(answer.getQuestionId()))
					.findFirst()
					.orElse(null);
			if (question != null && question.getCorrectOption() == answer.getSelectedOption()) {
				score++;
			}
		}
		return score;
	}

	// Get questions for quiz using the join entity.
	public List<Questions> getQuestionsForQuiz(Long quizId) {
		Quiz quiz = quizRepository.findById(quizId)
				.orElseThrow(() -> new RuntimeException("Quiz not found"));
		List<QuizQuestion> quizQuestions = quizQuestionRepository.findByQuiz(quiz);
		return quizQuestions.stream()
				.map(QuizQuestion::getQuestion)
				.collect(Collectors.toList());
	}

	// Method to invite multiple users to a quiz
	public void inviteUsersToQuiz(Long quizId, List<Long> userIds) {
		Quiz quiz = quizRepository.findById(quizId)
				.orElseThrow(() -> new EntityNotFoundException("Quiz not found"));
		List<Users> users = userRepository.findAllById(userIds);
		for (Users user : users) {
			Optional<com.mcq.entity.QuizInvite> existingInvite = quizInviteRepository.findByQuizIdAndUserId(quizId, user.getId());
			if (existingInvite.isEmpty()) {
				com.mcq.entity.QuizInvite quizInvite = new com.mcq.entity.QuizInvite();
				quizInvite.setQuiz(quiz);
				quizInvite.setUser(user);
				quizInvite.setInvitedStatus(true);
				quizInviteRepository.save(quizInvite);
			} else {
				com.mcq.entity.QuizInvite invite = existingInvite.get();
				invite.setInvitedStatus(true);
				quizInviteRepository.save(invite);
			}
		}
	}
}
