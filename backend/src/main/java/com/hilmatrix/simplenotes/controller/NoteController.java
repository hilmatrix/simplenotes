package com.hilmatrix.simplenotes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final JdbcTemplate jdbcTemplate;
    private final JwtDecoder jwtDecoder;

    @Autowired
    public NoteController(JdbcTemplate jdbcTemplate, JwtDecoder jwtDecoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.jwtDecoder = jwtDecoder;
    }

    @GetMapping
    public ResponseEntity<?> getNotes(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Extract the token from the Authorization header
            String token = authorizationHeader.replace("Bearer ", "");

            // Decode the JWT using JwtDecoder
            Jwt jwt = jwtDecoder.decode(token);

            // Extract email from the decoded JWT
            String email = jwt.getSubject();  // Assuming the email is the subject in the JWT

            // Query the users table to get the user_id based on the email
            String sql = "SELECT id FROM users WHERE email = ?";
            Integer userId = jdbcTemplate.queryForObject(sql, Integer.class, email);

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found for email: " + email);
            }

            // Query the notes table to get the id, title, and content for the user_id
            String notesSql = "SELECT id, title, content FROM notes WHERE user_id = ?";
            List<Map<String, Object>> notes = jdbcTemplate.queryForList(notesSql, userId);

            // Return the notes including the id, title, and content
            return ResponseEntity.ok(notes);

        } catch (Exception e) {
            // If decoding fails or any other exception occurs
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("JWT is invalid or error retrieving notes");
        }
    }
}
