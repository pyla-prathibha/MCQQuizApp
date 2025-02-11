# Quiz App Frontend (React)

This is the frontend application for the Quiz App built using React. It allows users to take quizzes on various technologies and submit their answers.

## Features

- Select a technology and quiz from the available options.
- Answer multiple-choice questions for the selected quiz.
- Submit the quiz and view the results.

<<<<<<< HEAD


=======
>>>>>>> 75d0bf5b664eb9acc9a91b2855492c3dcb970217
## Usage

1. Start the React development server: `npm start`
2. Open your browser and visit `http://localhost:3000` to access the Quiz App frontend.

## Configuration

The Quiz App frontend requires the backend API to be running. By default, it expects the backend to be running at `http://localhost:8080`. If your backend is running at a different address, you can update the API endpoint in the `src/api/apiConfig.js` file.

```javascript
// src/api/apiConfig.js

const BASE_URL = "http://localhost:8080/api"; // Update this URL if needed

export default BASE_URL;
```
