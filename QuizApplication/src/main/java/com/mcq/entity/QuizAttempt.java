package com.mcq.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Reference to the quiz (assumes you have a Quiz entity)
    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    // Reference to the user (assumes you have a Users entity)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    // Score obtained in the quiz
    @Column(name = "score", nullable = false)
    private int score;

    // Timestamp when the attempt was made
    @Column(name = "attempt_time")
    private LocalDateTime attemptTime;

    public QuizAttempt() {}

    // Getters and setters

}
