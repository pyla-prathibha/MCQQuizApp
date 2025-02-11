package com.mcq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mcq.entity.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    // Custom methods for user-related operations
    // Add this method to find a user by username
	public List<Users> findByUsername(String username);
}