package com.mcq.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class QuizSubmissionRequest {
    private Long quizId;
    private Long userId;
    private List<AnswerDTO> answers;

    // Getters and Setters

}
