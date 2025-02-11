import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      // Send the login request to your backend.
      const response = await axios.post("http://localhost:8080/login", {
        username: username,
        password: password,
      });

      // Expected response format: { id: 7, username: "johnDoe", role: "user" }
      const user = response.data;

      if (user.role === "admin") {
        navigate("/admin", { state: { userId: user.id, username: user.username } });
      } else if (user.role === "user") {
        navigate("/user", { state: { userId: user.id, username: user.username } });
      } else {
        setError("Invalid credentials");
      }
    } catch (err) {
      setError("Failed to log in");
      console.error("Login error:", err);
    }
  };

  return (
    <div className="container vh-100 d-flex flex-column justify-content-center align-items-center">
      {/* Heading */}
      <h1 className="mb-4 text-center text-primary">Quiz App</h1>

      {/* Card for the login form */}
      <div className="card shadow-sm" style={{ maxWidth: "400px", width: "100%" }}>
        <div className="card-header bg-primary text-white text-center">
          <h3 className="mb-0">Login</h3>
        </div>
        <div className="card-body">
          <form onSubmit={handleSubmit}>
            <div className="form-group mb-3">
              <label htmlFor="username" className="form-label">Username</label>
              <input
                type="text"
                id="username"
                className="form-control"
                placeholder="Enter your username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </div>
            <div className="form-group mb-3">
              <label htmlFor="password" className="form-label">Password</label>
              <input
                type="password"
                id="password"
                className="form-control"
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
            <button type="submit" className="btn btn-primary w-100">
              Log In
            </button>
            {error && <div className="alert alert-danger mt-3">{error}</div>}
          </form>
        </div>
      </div>
    </div>
  );
};

export default Login;
