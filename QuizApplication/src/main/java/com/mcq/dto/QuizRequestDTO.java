package com.mcq.dto;

import java.time.LocalDateTime;
import java.util.List;

public class QuizRequestDTO {
    private String quizName;
    private String technology;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Long> invitedUserIds;  // List of invited user IDs

    public QuizRequestDTO() {}

    public QuizRequestDTO(String quizName, String technology, LocalDateTime startTime, LocalDateTime endTime, List<Long> invitedUserIds) {
        this.quizName = quizName;
        this.technology = technology;
        this.startTime = startTime;
        this.endTime = endTime;
        this.invitedUserIds = invitedUserIds;
    }

    // Getters and Setters
    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<Long> getInvitedUserIds() {
        return invitedUserIds;
    }

    public void setInvitedUserIds(List<Long> invitedUserIds) {
        this.invitedUserIds = invitedUserIds;
    }
}
