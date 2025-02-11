package com.mcq.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.mcq.entity.*;
import com.mcq.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;


@Service
public class QuizService {
	@Autowired
	private final QuizRepository quizRepository;

	@Autowired
	private QuizQuestionRepository quizQuestionRepository;

	@Autowired
	private QuestionRepository questRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private QuizInviteRepository quizInviteRepository;




	public QuizService(QuizRepository quizRepository) {
		this.quizRepository = quizRepository;
	}


	public Quiz createQuiz(Quiz quiz, int numQuestions, List<Long> invitedUserIds) {
		List<Users> invitedUsers = userRepository.findAllById(invitedUserIds);

		// Fetch all available questions from the repository
		List<Questions> allQuestions = questRepository.findAll();

		// Ensure there are enough questions in the database
		if (allQuestions.size() < numQuestions) {
			throw new IllegalArgumentException("Not enough questions available to create the quiz.");
		}

		// Randomly select the required number of questions
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

		// Associate the questions with the quiz
		quiz.setQuizQuestions(quizQuestions);

		// Save the quiz first before using it in QuizInvite
		Quiz savedQuiz = quizRepository.save(quiz);

//		// Save invited users in quiz_invites
//		for (Users user : invitedUsers) {
//			QuizInvite invite = new QuizInvite(); // Create an instance first
//			invite.setQuiz(savedQuiz); // Use the saved quiz
//			invite.setUser(user); // Set user
//			quizInviteRepository.save(invite); // Save after setting values
//		}
//
//		// Set invited users (though this is just for reference and not stored in DB directly)
//		savedQuiz.setInvitedUsers(invitedUsers);

		List<QuizInvite> invites = new ArrayList<>();
		for (Users user : invitedUsers) {
			QuizInvite invite = new QuizInvite();
			invite.setQuiz(savedQuiz);
			invite.setUser(user);
			invites.add(invite);
		}
		quizInviteRepository.saveAll(invites); // Sa

		return savedQuiz;
	}



	public Quiz getQuizTime(Long id) {
		return quizRepository.findQuizTimeById(id);
	}


	public Quiz getQuizById(Long id) {
		return quizRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Quiz not found"));
	}

	public Quiz updateQuiz(Long id, Quiz updatedQuiz) {
		Quiz quiz = getQuizById(id);
		// Perform any necessary validation or business logic
		// Update the quiz entity with the new values
		quiz.setQuizName(updatedQuiz.getQuizName());
		quiz.setTechnology(updatedQuiz.getTechnology());
		return quizRepository.save(quiz);
	}

	public void deleteQuiz(Long id) {
		Quiz quiz = getQuizById(id);
		// Perform any necessary validation or business logic
		quizRepository.delete(quiz);
	}

	public List<Quiz> getAllQuizzes() {
		return quizRepository.findAll();
	}

	public List<Questions> getQuizQuestById(Long id){
		System.out.println("Id"+id);
		List<QuizQuestion> quizQuesList =  quizQuestionRepository.findByQuiz(getQuizById(id));
		List<Questions> questionList = new ArrayList<>();
		for(QuizQuestion quizQues: quizQuesList) {
			Optional<Questions> optionalQuestions = questRepository.findById(quizQues.getQuestion().getId());
			optionalQuestions.ifPresent(questionList::add);
		}

		questionList.forEach(q->System.out.println(q.getQuestionText()));
		return questionList;
	}



}