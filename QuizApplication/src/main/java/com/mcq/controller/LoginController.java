package com.mcq.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mcq.entity.Users;
import com.mcq.repository.UserRepository;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class LoginController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> credential) {
		System.out.println("In Login");

		String username = credential.get("username");
		String rawPassword = credential.get("password");

		List<Users> userByUname = userRepo.findByUsername(username);

		for (Users user : userByUname) {
			System.out.println("🔹 Checking password for user: " + username);
			System.out.println("🔹 Stored Hashed Password: " + user.getPassword());

			if (passwordEncoder.matches(rawPassword, user.getPassword())) {
				// Build the response JSON object
				Map<String, Object> response = new HashMap<>();
				response.put("id", user.getId());
				response.put("username", user.getUsername());
				response.put("role", user.isAdmin() ? "admin" : "user");

				return ResponseEntity.ok(response);
			}
		}

		// Return error response if credentials are invalid
		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("error", "Invalid credentials");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	}
}
