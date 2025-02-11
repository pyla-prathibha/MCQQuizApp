import React, { useEffect, useState } from "react";
import axios from "axios";
import Select from "react-select"; // Import react-select
import { HeaderNav } from "./HeaderNav";

const QuizGenerator = () => {
  const [questions, setQuestions] = useState([]);
  const [technologies, setTechnologies] = useState([]);
  const [selectedTechnology, setSelectedTechnology] = useState("");
  const [quizName, setQuizName] = useState("");
  const [quiz, setQuiz] = useState([]);
  const [numQuestions, setNumQuestions] = useState(5); // Default to 5 questions
  const [startTime, setStartTime] = useState("");
  const [endTime, setEndTime] = useState("");
  const [error, setError] = useState("");
  const [users, setUsers] = useState([]);
  const [selectedUsers, setSelectedUsers] = useState([]);

  useEffect(() => {
    fetchQuestions();
  }, []);

  const fetchQuestions = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/questions/getAllQuestion");
      setQuestions(response.data);

      const distinctTechnologies = [...new Set(response.data.map((question) => question.technology))];
      setTechnologies(distinctTechnologies);
    } catch (error) {
      setError("Failed to fetch questions");
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/users"); // Update endpoint as needed
      setUsers(response.data);
    } catch (error) {
      console.error("Failed to fetch users:", error);
    }
  };

  const handleTechnologyChange = (e) => {
    setSelectedTechnology(e.target.value);
  };

  const handleGenerateQuiz = async () => {
    setError("");

    if (!startTime || !endTime) {
      setError("Start time and End time are required.");
      return;
    }

    if (new Date(startTime) >= new Date(endTime)) {
      setError("End time must be later than Start time.");
      return;
    }

    if (numQuestions <= 0) {
      setError("Number of questions must be greater than 0.");
      return;
    }

    const filteredQuestions = questions.filter((question) => question.technology === selectedTechnology);

    if (filteredQuestions.length < numQuestions) {
      setError(`Only ${filteredQuestions.length} questions available for ${selectedTechnology}.`);
      return;
    }

    const selectedQuestions = getRandomItemsFromArray(filteredQuestions, numQuestions);

    setQuiz(selectedQuestions);

    const quizData = {
      quizName: quizName,
      technology: selectedTechnology,
      startTime: startTime,
      endTime: endTime,
      numQuestions: numQuestions,
      invitedUsers: selectedUsers.map((user) => user.value), // Store only the user IDs from the selected options
    };

    console.log("Sending quiz data:", quizData); // Debugging
    try {
      console.log("Sending quiz data:", JSON.stringify(quizData, null, 2));
      
      const response = await axios.post("http://localhost:8080/api/quizzes", quizData);
      
      if (response.data.id) {
        console.log("Quiz created successfully:", response.data);
    
        const invitePayload = { quizId: response.data.id, userIds: quizData.invitedUsers };
    
        console.log("Sending invitation payload:", JSON.stringify(invitePayload, null, 2));
    
        const inviteResponse = await axios.post("http://localhost:8080/api/quizzes/invite-users", invitePayload);
        
        console.log("Invitations sent successfully:", inviteResponse.data);
      } else {
        console.error("Quiz creation failed. Response did not contain an ID.");
      }
    } catch (error) {
      if (error.response) {
        console.error("Error Response Data:", error.response.data);
        console.error("Error Status Code:", error.response.status);
      } else {
        console.error("Network or Unknown Error:", error);
      }
    }
  }
    

  const getRandomItemsFromArray = (array, count) => {
    const shuffledArray = [...array].sort(() => Math.random() - 0.5);
    return shuffledArray.slice(0, count);
  };

  // Prepare user data for react-select
  const userOptions = users.map((user) => ({
    value: user.id,
    label: user.username,
  }));

  return (
    <div>
      <HeaderNav />
      <div className="container">
        <h2 className="mt-3">Quiz Generator</h2>
        <div className="form-group">
          <label htmlFor="quizName">Quiz Name:</label>
          <input
            type="text"
            className="form-control"
            id="quizName"
            value={quizName}
            onChange={(e) => setQuizName(e.target.value)}
          />
        </div>
        <div className="form-group">
          <label htmlFor="selectedTechnology">Select Technology:</label>
          <select
            className="form-control"
            id="selectedTechnology"
            value={selectedTechnology}
            onChange={handleTechnologyChange}
          >
            <option value="">Select</option>
            {technologies.map((technology) => (
              <option value={technology} key={technology}>
                {technology}
              </option>
            ))}
          </select>
        </div>
        <div className="form-group"> 
          <label htmlFor="numQuestions">Number of Questions:</label>
          <input
            type="number"
            className="form-control"
            id="numQuestions"
            value={numQuestions}
            min="1"
            onChange={(e) => setNumQuestions(Number(e.target.value))}
          />
        </div>
        <div className="form-group">
          <label htmlFor="startTime">Start Time:</label>
          <input
            type="datetime-local"
            className="form-control"
            id="startTime"
            value={startTime}
            onChange={(e) => setStartTime(e.target.value)}
          />
        </div>
        <div className="form-group">
          <label htmlFor="endTime">End Time:</label>
          <input
            type="datetime-local"
            className="form-control"
            id="endTime"
            value={endTime}
            onChange={(e) => setEndTime(e.target.value)}
          />
        </div>

        <div className="form-group">
          <label htmlFor="selectedUsers">Select Users:</label>
          <Select
            isMulti
            options={userOptions}
            value={selectedUsers}
            onChange={setSelectedUsers}
            getOptionLabel={(e) => e.label}
            getOptionValue={(e) => e.value}
          />
        </div>

        <button
          className="btn btn-primary mt-3"
          onClick={handleGenerateQuiz}
          disabled={!selectedTechnology || !quizName || !startTime || !endTime}
        >
          Generate Quiz
        </button>

        {quiz.length > 0 && (
          <div>
            <h3 className="mt-3">Quiz Questions</h3>
            <ul className="list-group">
              {quiz.map((question) => (
                <li className="list-group-item" key={question.id}>
                  {question.questionText}
                </li>
              ))}
            </ul>
          </div>
        )}

        {error && <div className="text-danger mt-3">{error}</div>}
      </div>
    </div>
  );
};

export default QuizGenerator;
