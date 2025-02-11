package com.mcq.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mcq.entity.Users;
import com.mcq.repository.UserRepository;

import java.util.List;


@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean authenticate(String username, String rawPassword) {
        List<Users> users = userRepository.findByUsername(username);

        if (users.isEmpty()) {
            System.out.println("❌ User not found: " + username);
            return false;
        }

        Users user = users.get(0);
        System.out.println("🔹 Stored Hashed Password: " + user.getPassword());
        System.out.println("🔹 Entered Raw Password: " + rawPassword);

        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());

        if (matches) {
            System.out.println("✅ Password matches!");
        } else {
            System.out.println("❌ Invalid password!");
        }

        return matches;
    }
}
