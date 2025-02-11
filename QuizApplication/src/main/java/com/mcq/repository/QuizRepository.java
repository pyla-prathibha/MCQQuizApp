package com.mcq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mcq.entity.Quiz;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    // Fetch quiz time by ID
    @Query("SELECT q FROM Quiz q WHERE q.id = :id")
    Quiz findQuizTimeById(Long id);


}
