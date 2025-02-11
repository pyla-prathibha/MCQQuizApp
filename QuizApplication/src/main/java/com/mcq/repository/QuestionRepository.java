package com.mcq.repository;

import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mcq.entity.Questions;

import java.util.List;
import java.util.Random;

@Repository
public interface QuestionRepository extends JpaRepository<Questions, Long> { // ✅ Correct entity
    //List<Questions> findByQuizId(Long quizId); // ✅ Ensure return type is correct
}
