import React, { useEffect, useState } from "react";
import axios from "axios";
import { useLocation, useNavigate } from "react-router-dom";
import UserHeaderNav from "./UserHeaderNav";

const QuizPage = () => {
  // State variables
  const [quizList, setQuizList] = useState([]);
  const [selectedTechnology, setSelectedTechnology] = useState("");
  const [selectedQuiz, setSelectedQuiz] = useState("");
  const [selectedOptions, setSelectedOptions] = useState([]);
  const [error, setError] = useState("");
  const [quizQuest, setQuizQuest] = useState([]);
  const [totalMarks, setTotalMarks] = useState(0);
  const [quizTime, setQuizTime] = useState({ startTime: "", endTime: "" });
  const [timeLeft, setTimeLeft] = useState(null);
  // Invitation status: null = not yet checked, false = not invited, true = invited.
  const [isInvited, setIsInvited] = useState(null);
  // attempt holds existing attempt details if found (prevents retakes)
  const [attempt, setAttempt] = useState(null);
  // submitted tracks whether the test has been submitted in this session
  const [submitted, setSubmitted] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  // Hooks for location and navigation
  const location = useLocation();
  const { userId, username } = location.state || {};
  const navigate = useNavigate();

  console.log("Logged-in userId:", userId, "Username:", username);

  // Fetch quiz list on mount
  useEffect(() => {
    fetchQuizList();
  }, []);

  // When a quiz is selected (or userId changes), fetch questions, quiz time, and check invitation and existing attempt.
  useEffect(() => {
    if (selectedQuiz && userId) {
      fetchQuizQuest(selectedQuiz);
      fetchQuizTime(selectedQuiz);
      checkUserInvitation(selectedQuiz);
      checkQuizAttempt(selectedQuiz);
    }
  }, [selectedQuiz, userId]);

  // Countdown timer for quiz time
  useEffect(() => {
    if (quizTime.startTime && quizTime.endTime) {
      const interval = setInterval(() => {
        const now = new Date();
        const start = new Date(quizTime.startTime);
        const end = new Date(quizTime.endTime);
        if (now < start) {
          setTimeLeft("Quiz hasn't started yet.");
        } else if (now > end) {
          setTimeLeft("Time is up! Quiz ended.");
          clearInterval(interval);
        } else {
          const remainingTime = Math.max(0, (end - now) / 1000);
          setTimeLeft(formatTime(remainingTime));
        }
      }, 1000);
      return () => clearInterval(interval);
    }
  }, [quizTime]);

  // Auto-submit if time is up and the quiz is not submitted yet
  useEffect(() => {
    if (timeLeft === "Time is up! Quiz ended." && !submitted) {
      console.log("Auto-submitting quiz because time is up.");
      handleSubmit(); // Trigger auto-submit
    }
  }, [timeLeft, submitted]);

  // --- Functions ---

  const fetchQuizList = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/quizzes");
      setQuizList(response.data);
      setIsLoading(false);
    } catch (err) {
      setError("Failed to fetch quiz list");
      setIsLoading(false);
    }
  };

  const fetchQuizQuest = async (quizId) => {
    try {
      const response = await axios.get(`http://localhost:8080/api/quizzes/getQuizQuestById/${quizId}`);
      setQuizQuest(response.data);
      // Reset selected options for a new quiz
      setSelectedOptions([]);
    } catch (err) {
      setError("Failed to fetch quiz questions");
    }
  };

  const fetchQuizTime = async (quizId) => {
    try {
      const response = await axios.get(`http://localhost:8080/api/quizzes/getQuizTime/${quizId}`);
      const { startTime, endTime } = response.data;
      setQuizTime({ startTime, endTime });
    } catch (err) {
      setError("Failed to fetch quiz time");
    }
  };

  const checkUserInvitation = async (quizId) => {
    try {
      console.log("Checking invitation for quiz:", quizId, "with userId:", userId);
      const response = await axios.get(`http://localhost:8080/api/quizzes/isUserInvited/${quizId}?userId=${userId}`);
      console.log("Invitation API Response:", response.data);
      const invitedValue = response.data.isInvited === "true" || response.data.isInvited === true;
      console.log("Setting isInvited state to:", invitedValue);
      setIsInvited(invitedValue);
    } catch (err) {
      if (err.response && err.response.status === 403) {
        console.log("User is not invited:", err.response.data);
        setIsInvited(false);
      } else {
        console.error("Failed to check invitation status", err);
        setError("Failed to check invitation status");
      }
    }
  };

  const checkQuizAttempt = async (quizId) => {
    try {
      const response = await axios.get(`http://localhost:8080/api/quizzes/attempt?quizId=${quizId}&userId=${userId}`);
      console.log("Quiz Attempt API Response:", response.data);
      setAttempt(response.data);
      setSubmitted(true);
    } catch (err) {
      if (err.response && err.response.status === 404) {
        // No attempt exists; allow the quiz to be taken.
        setAttempt(null);
        setSubmitted(false);
      } else {
        console.error("Failed to check quiz attempt", err);
        setError("Failed to check quiz attempt");
      }
    }
  };

  const formatTime = (seconds) => {
    const minutes = Math.floor(seconds / 60);
    const sec = Math.floor(seconds % 60);
    return `${minutes}m ${sec}s`;
  };

  const handleTechnologyChange = (e) => {
    setSelectedTechnology(e.target.value);
    setSelectedQuiz("");
    setQuizQuest([]);
    setQuizTime({ startTime: "", endTime: "" });
    setIsInvited(null);
    setSubmitted(false);
    setAttempt(null);
  };

  const handleQuizChange = (e) => {
    setSelectedQuiz(e.target.value);
    setIsInvited(null);
    setSubmitted(false);
    setAttempt(null);
  };

  const handleOptionChange = (questionId, optionNumber) => {
    if (submitted) return; // Do not allow changes after submission
    setSelectedOptions((prevSelectedOptions) => {
      const updatedOptions = [...prevSelectedOptions];
      const questionIndex = updatedOptions.findIndex(
        (option) => option.questionId === questionId
      );
      if (questionIndex !== -1) {
        updatedOptions[questionIndex] = { questionId, optionNumber };
      } else {
        updatedOptions.push({ questionId, optionNumber });
      }
      return updatedOptions;
    });
  };

  // Handle quiz submission (manual or auto-triggered)
  const handleSubmit = async (e) => {
    if (e && e.preventDefault) e.preventDefault();
    if (submitted) return; // Prevent double submission

    const now = new Date();
    const end = new Date(quizTime.endTime);
    if (now > end) {
      setError("Cannot submit! Quiz time has ended.");
      return;
    }
    try {
      let marks = 0;
      // Process each answer
      for (const userAnswer of selectedOptions) {
        const { questionId, optionNumber } = userAnswer;
        // Optionally post each answer; this example posts them individually.
        await axios.post("http://localhost:8080/api/user-answers", {
          userId: userId,
          questionId: questionId,
          selectedOption: optionNumber,
        });
        const selectedQuestion = quizQuest.find(
          (question) => question.id === questionId
        );
        if (selectedQuestion && selectedQuestion.correctOption === optionNumber) {
          marks++;
        }
      }
      setTotalMarks(marks);
      // Mark as submitted and update the attempt state locally so that the form is locked.
      setSubmitted(true);
      setAttempt({ score: marks });
      console.log("Test submitted. Score:", marks);
      // Optionally, navigate to a results page:
      // navigate("/results", { state: { totalMarks: marks, username: username, quizId: selectedQuiz } });
    } catch (err) {
      setError("Failed to submit quiz");
      console.error("Error submitting quiz:", err);
    }
  };

  if (!userId) {
    return (
      <div className="container mt-4 text-center text-danger">
        Error: User ID is missing. Please log in again.
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="container mt-4 text-center">
        <h4>Loading...</h4>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container mt-4 text-center text-danger">
        {error}
      </div>
    );
  }

  const distinctTechnologies = [...new Set(quizList.map((quiz) => quiz.technology))];

  return (
    <div>
      <UserHeaderNav username={username} />
      <div className="container mt-4">
        <h2 className="mb-4">Quiz</h2>
        <form className="mb-4">
          {/* Technology Selection */}
          <div className="mb-3">
            <label className="form-label">Select Technology:</label>
            <select
              className="form-select"
              value={selectedTechnology}
              onChange={handleTechnologyChange}
              disabled={submitted}
            >
              <option value="">Select</option>
              {distinctTechnologies.map((technology) => (
                <option key={technology} value={technology}>
                  {technology}
                </option>
              ))}
            </select>
          </div>
          {/* Quiz Selection */}
          {selectedTechnology && (
            <div className="mb-3">
              <label className="form-label">Select Quiz:</label>
              <select
                className="form-select"
                value={selectedQuiz}
                onChange={handleQuizChange}
                disabled={submitted}
              >
                <option value="">Select</option>
                {quizList
                  .filter((quiz) => quiz.technology === selectedTechnology)
                  .map((quiz) => (
                    <option key={quiz.id} value={quiz.id}>
                      {quiz.quizName}
                    </option>
                  ))}
              </select>
            </div>
          )}
          {/* Quiz Timing */}
          {selectedQuiz && (
            <div className="mb-3">
              <h4>Quiz Duration</h4>
              <p>Start Time: {new Date(quizTime.startTime).toLocaleString()}</p>
              <p>End Time: {new Date(quizTime.endTime).toLocaleString()}</p>
              <p>
                Time Left: {timeLeft !== null ? <strong>{timeLeft}</strong> : "Loading..."}
              </p>
            </div>
          )}
          {/* Invitation Status */}
          {selectedQuiz && isInvited === false && (
            <div className="alert alert-warning">
              You are not invited to take this quiz.
            </div>
          )}
          {selectedQuiz && isInvited === null && (
            <div className="alert alert-info">
              Checking invitation status...
            </div>
          )}
          {/* Test Completed */}
          {selectedQuiz && attempt && (
            <div className="alert alert-success">
              You have successfully completed this quiz. Your score: {attempt.score}
            </div>
          )}
          {/* Quiz Questions */}
          {selectedQuiz && isInvited === true && !attempt && quizQuest.length > 0 && timeLeft && !timeLeft.includes("ended") && (
            <div>
              <h4>Quiz Questions:</h4>
              {quizQuest.map((question) => (
                <div key={question.id} className="mb-3">
                  <h5>{question.questionText}</h5>
                  {[1, 2, 3, 4].map((option) => (
                    <div key={option} className="form-check">
                      <input
                        type="radio"
                        className="form-check-input"
                        name={`question-${question.id}`}
                        value={option}
                        checked={
                          selectedOptions.find((opt) => opt.questionId === question.id)
                            ?.optionNumber === option
                        }
                        onChange={() => handleOptionChange(question.id, option)}
                        disabled={submitted}
                      />
                      <label className="form-check-label">
                        Option {option}: {question[`option${option}`]}
                      </label>
                    </div>
                  ))}
                </div>
              ))}
              {!submitted && (
                <button type="submit" className="btn btn-primary mt-3" onClick={handleSubmit}>
                  Submit
                </button>
              )}
            </div>
          )}
        </form>
        {/* Score Display */}
        {submitted && !attempt && (
          <div className="alert alert-success">
            Test submitted! Your score: {totalMarks}
          </div>
        )}
      </div>
    </div>
  );
};

export default QuizPage;
