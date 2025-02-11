package com.mcq.repository;

import com.mcq.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    // Find a quiz attempt by quiz and user
    Optional<QuizAttempt> findByQuiz_IdAndUser_Id(Long quizId, Long userId);
}
