package com.mcq.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

//import javax.persistence.*;

@Entity
@Data
@Table(name = "quiz_invites")
public class QuizInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

//    List<String> userNames = new ArrayList<>();
@ManyToOne
@JoinColumn(name = "user_id", nullable = false)
private Users user;  // ✅ Store user reference
    public QuizInvite() {}

    // Getters and Setters
}
