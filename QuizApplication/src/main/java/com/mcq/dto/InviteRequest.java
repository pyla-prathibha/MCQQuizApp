package com.mcq.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InviteRequest {
    private Long quizId;

    @JsonProperty("userIds")  // This maps JSON property "userIds" to this field.
    private List<Long> invitedUserNumbers;

    // Getters and setters
    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public List<Long> getInvitedUserNumbers() {
        return invitedUserNumbers;
    }

    public void setInvitedUserNumbers(List<Long> invitedUserNumbers) {
        this.invitedUserNumbers = invitedUserNumbers;
    }
}
