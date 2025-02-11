import React, { useEffect, useState } from "react";
import axios from "axios";
import { useLocation } from "react-router-dom";
import UserHeaderNav from "./UserHeaderNav";
import { useNavigate } from "react-router-dom"; // Import useNavigate


const QuizPage = () => {
  const [quizList, setQuizList] = useState([]);
  const [selectedTechnology, setSelectedTechnology] = useState("");
  const [selectedQuiz, setSelectedQuiz] = useState("");
  const [selectedOptions, setSelectedOptions] = useState([]);
  const [error, setError] = useState("");
  const [quizQuest, setQuizQuest] = useState([]);
  const [totalMarks, setTotalMarks] = useState(0);
  const [quizTime, setQuizTime] = useState({ startTime: "", endTime: "" });
  const [timeLeft, setTimeLeft] = useState(null);

  const location = useLocation();
  const username = location.state?.username;

  useEffect(() => {
    fetchQuizList();
  }, []);

  useEffect(() => {
    if (selectedQuiz) {
      fetchQuizQuest(selectedQuiz);
      fetchQuizTime(selectedQuiz);
    }
  }, [selectedQuiz]);

  useEffect(() => {
    if (quizTime.startTime && quizTime.endTime) {
      const interval = setInterval(() => {
        const now = new Date();
        const end = new Date(quizTime.endTime);
        const start = new Date(quizTime.startTime);

        if (now < start) {
          setTimeLeft("Quiz hasn't started yet.");
        } else if (now > end) {
          setTimeLeft("Time is up! Quiz ended.");
          clearInterval(interval); // Stop countdown when time is up
        } else {
          const remainingTime = Math.max(0, (end - now) / 1000);
          setTimeLeft(formatTime(remainingTime));
        }
      }, 1000);

      return () => clearInterval(interval);
    }
  }, [quizTime]);

  const fetchQuizList = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/quizzes");
      setQuizList(response.data);
    } catch (error) {
      setError("Failed to fetch quiz list");
    }
  };

  const fetchQuizQuest = async (quizId) => {
    try {
      const response = await axios.get(
        `http://localhost:8080/api/quizzes/getQuizQuestById/${quizId}`
      );
      setQuizQuest(response.data);
      setSelectedOptions([]);
    } catch (error) {
      setError("Failed to fetch quiz questions");
    }
  };

  const fetchQuizTime = async (quizId) => {
    try {
      const response = await axios.get(
        `http://localhost:8080/api/quizzes/getQuizTime/${quizId}`
      );
      const { startTime, endTime } = response.data;
      setQuizTime({ startTime, endTime });
    } catch (error) {
      setError("Failed to fetch quiz time");
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
  };

  const handleQuizChange = (e) => {
    setSelectedQuiz(e.target.value);
  };

  const handleOptionChange = (questionId, optionNumber) => {
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


  const navigate = useNavigate(); // Initialize navigation
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    const now = new Date();
    const end = new Date(quizTime.endTime);
  
    if (now > end) {
      setError("Cannot submit! Quiz time has ended.");
      return;
    }
  
    try {
      let marks = 0;
      for (const userAnswer of selectedOptions) {
        const { questionId, optionNumber } = userAnswer;
        const userAnswers = {
          userId: 6, // Replace with actual user ID
          questionId,
          selectedOption: optionNumber,
        };
  
        await axios.post("http://localhost:8080/api/user-answers", userAnswers);
  
        // Check if the answer is correct
        const selectedQuestion = quizQuest.find(
          (question) => question.id === questionId
        );
        if (selectedQuestion.correctOption === optionNumber) {
          marks += 1;
        }
      }
      setTotalMarks(marks);
  
      // Debugging: Check if navigation is triggered
      console.log("Navigating to results page with marks:", marks);
  
      // Navigate to the results page
      navigate("/results", {
        state: { totalMarks: marks, username: username, quizId: selectedQuiz },
      });
    } catch (error) {
      setError("Failed to submit quiz");
      console.error("Error submitting quiz:", error);
    }
  };
  

  if (error) {
    return <div>{error}</div>;
  }

  const distinctTechnologies = [
    ...new Set(quizList.map((quiz) => quiz.technology)),
  ];

  return (
    <div>
      <UserHeaderNav username={username} />
      <div className="container">
        <h2 className="mt-3">Quiz</h2>
        <form className="mt-3">
          <div className="mb-3">
            <label className="form-label">Select Technology:</label>
            <select
              className="form-select"
              value={selectedTechnology}
              onChange={handleTechnologyChange}
            >
              <option value="">Select</option>
              {distinctTechnologies.map((technology) => (
                <option key={technology} value={technology}>
                  {technology}
                </option>
              ))}
            </select>
          </div>
          {selectedTechnology && (
            <div className="mb-3">
              <label className="form-label">Select Quiz:</label>
              <select
                className="form-select"
                value={selectedQuiz}
                onChange={handleQuizChange}
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

          {selectedQuiz && (
            <div>
              <h4>Quiz Duration</h4>
              <p>Start Time: {new Date(quizTime.startTime).toLocaleString()}</p>
              <p>End Time: {new Date(quizTime.endTime).toLocaleString()}</p>
              <p>
                Time Left:{" "}
                {timeLeft !== null ? (
                  <strong>{timeLeft}</strong>
                ) : (
                  "Loading..."
                )}
              </p>
            </div>
          )}

          {selectedQuiz && quizQuest.length > 0 && timeLeft && !timeLeft.includes("ended") && (
            <div>
              <h4>Quiz Questions:</h4>
              {quizQuest.map((question) => (
                <div key={question.id} className="mb-3">
                  <h4>{question.questionText}</h4>
                  {[1, 2, 3, 4].map((option) => (
                    <div key={option}>
                      <input
                        type="radio"
                        name={`question-${question.id}`}
                        value={option}
                        checked={
                          selectedOptions.find((opt) => opt.questionId === question.id)
                            ?.optionNumber === option
                        }
                        onChange={() => handleOptionChange(question.id, option)}
                      />
                      <label>Option {option}: {question[`option${option}`]}</label>
                    </div>
                  ))}
                </div>
              ))}
              <button type="submit" className="btn btn-primary" onClick={handleSubmit}>
                Submit
              </button>
            </div>
          )}
        </form>
        {totalMarks > 0 && (
          <div className="mt-3">
            <h4>Total Marks: {totalMarks}</h4>
          </div>
        )}
      </div>
    </div>
  );
};

export default QuizPage;
