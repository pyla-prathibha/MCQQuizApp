import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./Login";
import AdminLogin from "./AdminLogin";
import ShowQuestions from "./ShowQuestions";
import QuizGenerator from "./QuizGenerator";
import QuizPage from "./QuizPage";
import ShowUserResponse from "./ShowUserResponse";
import ResultsPage from "./ResultsPage";

const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/admin" element={<AdminLogin />} />
        <Route path="/user" element={<QuizPage />} />
        <Route path="/quiz" element={<QuizPage />} />
        <Route path="/results" element={<ResultsPage />} />
        <Route path="/admin/show-question" element={<ShowQuestions />} />
        <Route path="/admin/generate-quiz" element={<QuizGenerator />} />
        <Route path="/admin/validate-answer" element={<ShowUserResponse />} />
      </Routes>
    </Router>
  );
};

export default App;
