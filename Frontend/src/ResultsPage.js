import React from "react";
import { useLocation, useNavigate } from "react-router-dom";

const ResultsPage = () => {
  const location = useLocation();
  const navigate = useNavigate();

  // Check if the state exists
  const { totalMarks, username, quizId } = location.state || {};

  return (
    <div className="container">
      <h2>Quiz Results</h2>
      {totalMarks !== undefined ? (
        <div>
          <p><strong>Username:</strong> {username}</p>
          <p><strong>Quiz ID:</strong> {quizId}</p>
          <h3>Your Score: {totalMarks}</h3>
        </div>
      ) : (
        <p>No results found. Please attempt a quiz first.</p>
      )}

      <button className="btn btn-primary mt-3" onClick={() => navigate("/")}>
        Go to Home
      </button>
    </div>
  );
};

export default ResultsPage; // ✅ Ensure this line exists!
