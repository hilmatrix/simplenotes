package com.hilmatrix.simplenotes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<?> getNoteById(@PathVariable("id") Integer noteId, @RequestHeader("Authorization") String authorizationHeader) {
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

            // Query the notes table to get the note by id for the user_id
            String noteSql = "SELECT id, title, content FROM notes WHERE user_id = ? AND id = ?";
            Map<String, Object> note = jdbcTemplate.queryForMap(noteSql, userId, noteId);

            if (note == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found for id: " + noteId);
            }

            // Return the note including the id, title, and content
            return ResponseEntity.ok(note);

        } catch (Exception e) {
            // If decoding fails or any other exception occurs
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("JWT is invalid or error retrieving note");
        }
    }

    @PostMapping
    public ResponseEntity<?> createNote(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody Map<String, Object> noteData) {
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

            // Retrieve note data from the request body
            String title = (String) noteData.get("title");
            String content = (String) noteData.get("content");

            // Find the current max ID in the notes table and increment it for the new ID
            String maxIdSql = "SELECT COALESCE(MAX(id), 0) + 1 FROM notes";
            Integer newNoteId = jdbcTemplate.queryForObject(maxIdSql, Integer.class);

            // Insert the new note
            String insertSql = "INSERT INTO notes (id, user_id, title, content) VALUES (?, ?, ?, ?)";
            int rowsInserted = jdbcTemplate.update(insertSql, newNoteId, userId, title, content);

            if (rowsInserted > 0) {
                // Return the created note's ID and details
                Map<String, Object> response = Map.of(
                        "id", newNoteId,
                        "user_id", userId,
                        "title", title,
                        "content", content
                );
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create note");
            }

        } catch (Exception e) {
            // If decoding fails or any other exception occurs
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("JWT is invalid or error creating note");
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateNoteById(
            @PathVariable("id") Integer noteId,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody Map<String, Object> noteData) {
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

            // Retrieve note data from the request body
            String title = (String) noteData.get("title");
            String content = (String) noteData.get("content");

            // Check if the note exists for the user
            String checkNoteSql = "SELECT COUNT(*) FROM notes WHERE user_id = ? AND id = ?";
            Integer count = jdbcTemplate.queryForObject(checkNoteSql, Integer.class, userId, noteId);

            if (count == null || count == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found for id: " + noteId);
            }

            // Update the note
            String updateSql = "UPDATE notes SET title = ?, content = ? WHERE id = ? AND user_id = ?";
            int rowsUpdated = jdbcTemplate.update(updateSql, title, content, noteId, userId);

            if (rowsUpdated > 0) {
                return ResponseEntity.ok("Note updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update note");
            }

        } catch (Exception e) {
            // If decoding fails or any other exception occurs
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("JWT is invalid or error updating note");
        }
    }
}
