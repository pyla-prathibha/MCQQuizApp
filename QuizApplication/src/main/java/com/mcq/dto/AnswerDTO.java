package com.mcq.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AnswerDTO {
    private Long questionId;
    private Integer selectedOption; // e.g., 1, 2, 3, or 4

    // Getters and Setters

}
