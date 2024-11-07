package com.hilmatrix.simplenotes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class AuthController {

    private final JwtEncoder jwtEncoder;

    public AuthController(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;  // JDBC template for direct DB interaction

    @Autowired
    private PasswordEncoder passwordEncoder;  // To check password hash

    @PostMapping
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        // Query to find the user by email
        String sql = "SELECT password_hash FROM users WHERE email = ?";
        try {
            // Execute the query
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, loginRequest.getEmail());

            // If email found, verify password
            String storedPasswordHash = (String) result.get("password_hash");
            if (passwordEncoder.matches(loginRequest.getPassword(), storedPasswordHash)) {
                JwtClaimsSet claims = JwtClaimsSet.builder()
                        .issuer("your-issuer")  // You can set your desired issuer here
                        .subject(loginRequest.getEmail()) // User's email as the subject
                        .issuedAt(Instant.now()) // Set the issued time
                        .expiresAt(Instant.now().plusSeconds(3600)) // Set the expiration time (e.g., 1 hour)
                        .build();

                Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims));

                // Return JSON response with the token
                Map<String, String> response = new HashMap<>();
                response.put("token", jwt.getTokenValue());
                return ResponseEntity.ok(response);
            } else {
                // Return JSON response with error message
                Map<String, String> response = new HashMap<>();
                response.put("result", "wrong password");
                return ResponseEntity.status(401).body(response);
            }
        } catch (Exception e) {
            // If email not found in database
            Map<String, String> response = new HashMap<>();
            response.put("result", "user with email " + loginRequest.getEmail() + " does not exist");
            return ResponseEntity.status(404).body(response);
        }
    }
}
