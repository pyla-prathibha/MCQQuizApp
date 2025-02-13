package com.mcq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mcq.entity.Quiz;
import com.mcq.entity.QuizQuestion;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    // Custom methods for quiz question-related operations
	public List<QuizQuestion> findByQuiz(Quiz quiz);
}