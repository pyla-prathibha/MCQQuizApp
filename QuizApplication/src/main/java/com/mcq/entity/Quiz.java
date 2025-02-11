package com.mcq.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;


import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name="quizzes")
public class Quiz {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String quizName;

	@Column(nullable = false)
	private String technology;

	@Column(nullable = false)
	private LocalDateTime startTime;

	@Column(nullable = false)
	private LocalDateTime endTime;

	@JsonIgnoreProperties("quiz")
	@OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
	private List<QuizQuestion> quizQuestions;

	// **NEW: Many-to-Many Relationship for Invited Users**
	@ManyToMany
	@JoinTable(
			name = "quiz_invites",
			joinColumns = @JoinColumn(name = "quiz_id"),
			inverseJoinColumns = @JoinColumn(name = "user_id")
	)
	private List<Users> invitedUsers;

	// **Constructors**
	public Quiz(Long id, String quizName, String technology, LocalDateTime startTime, LocalDateTime endTime, List<QuizQuestion> quizQuestions, List<Users> invitedUsers) {
		this.id = id;
		this.quizName = quizName;
		this.technology = technology;
		this.startTime = startTime;
		this.endTime = endTime;
		this.quizQuestions = quizQuestions;
		this.invitedUsers = invitedUsers;
	}

	public Quiz(String quizName, String technology, LocalDateTime startTime, LocalDateTime endTime, List<QuizQuestion> quizQuestions, List<Users> invitedUsers) {
		this.quizName = quizName;
		this.technology = technology;
		this.startTime = startTime;
		this.endTime = endTime;
		this.quizQuestions = quizQuestions;
		this.invitedUsers = invitedUsers;
	}

	public Quiz() {}

	// **Getters & Setters**
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public List<QuizQuestion> getQuizQuestions() {
		return quizQuestions;
	}

	public void setQuizQuestions(List<QuizQuestion> quizQuestions) {
		this.quizQuestions = quizQuestions;
	}

	// ✅ Add Getter and Setter for invitedUsers
	public List<Users> getInvitedUsers() {
		return invitedUsers;
	}

	public void setInvitedUsers(List<Users> invitedUsers) {
		this.invitedUsers = invitedUsers;
	}
}
