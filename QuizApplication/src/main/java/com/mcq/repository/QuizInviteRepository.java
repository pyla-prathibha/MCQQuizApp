package com.mcq.repository;

import com.mcq.entity.QuizInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizInviteRepository extends JpaRepository<QuizInvite, Long> {
    boolean existsByQuizIdAndUserId(Long quizId, Long userId);
}

