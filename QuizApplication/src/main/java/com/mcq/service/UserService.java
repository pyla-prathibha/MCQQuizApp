package com.mcq.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mcq.entity.Users;
import com.mcq.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Users createUser(Users user) {
        // Hash password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public Users getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public Users updateUser(Long id, Users updatedUser) {
        Users user = getUserById(id);

        // Update fields
        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setAdmin(updatedUser.isAdmin());

        // Hash new password if updated
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        Users user = getUserById(id);
        userRepository.delete(user);
    }

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }
}
