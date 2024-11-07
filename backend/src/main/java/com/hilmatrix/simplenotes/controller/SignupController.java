package com.hilmatrix.simplenotes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SignupController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, Object> signupData) {
        try {
            // Extract and validate input fields
            String name = (String) signupData.get("name");
            String email = (String) signupData.get("email");
            String password = (String) signupData.get("password");

            if (name == null || name.isEmpty() ||
                    email == null || email.isEmpty() ||
                    password == null || password.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("All fields (name, email, password) are required.");
            }

            // Encode the password
            String passwordHash = passwordEncoder.encode(password);

            // Insert the new user into the database
            String insertSql = "INSERT INTO users (name, email, password_hash) VALUES (?, ?, ?)";
            int rowsInserted = jdbcTemplate.update(insertSql, name, email, passwordHash);

            if (rowsInserted > 0) {
                return ResponseEntity.status(HttpStatus.CREATED).body("User signed up successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to sign up user");
            }

        } catch (Exception e) {
            // Handle potential database errors (e.g., duplicate email)
            if (e.getMessage().contains("duplicate key")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while signing up user");
        }
    }
}
