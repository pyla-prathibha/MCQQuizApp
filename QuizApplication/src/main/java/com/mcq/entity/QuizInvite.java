package com.mcq.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "quiz_invites")
public class QuizInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;


    @Column(name = "invited_status")
    private boolean invitedStatus; // This column tracks if the user is invited

    public QuizInvite() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public boolean isInvitedStatus() {
        return invitedStatus;
    }

    public void setInvitedStatus(boolean invitedStatus) {
        this.invitedStatus = invitedStatus;
    }
}
