package com.mcq.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
	private PasswordEncoder passwordEncoder; // ✅ Inject PasswordEncoder

	@PostMapping("/login")
	public String Login(@RequestBody Map<String, String> credential) {
		System.out.println("In Login");

		String username = credential.get("username");
		String rawPassword = credential.get("password");

		List<Users> userByUname = userRepo.findByUsername(username);

		for (Users user : userByUname) {
			System.out.println("🔹 Checking password for user: " + username);
			System.out.println("🔹 Stored Hashed Password: " + user.getPassword());

			if (passwordEncoder.matches(rawPassword, user.getPassword())) { // ✅ Use PasswordEncoder
				if (user.isAdmin()) {
					return "admin";
				} else {
					return "user";
				}
			}
		}

		return "invalid";
	}
}
