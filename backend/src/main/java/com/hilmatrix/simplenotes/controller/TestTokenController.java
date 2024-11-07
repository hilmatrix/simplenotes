package com.hilmatrix.simplenotes.controller;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestTokenController {

    private final JwtDecoder jwtDecoder;

    public TestTokenController(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @GetMapping("/testtoken")
    public String testToken(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Extract the token from the Authorization header
            String token = authorizationHeader.replace("Bearer ", "");

            // Decode the JWT using JwtDecoder
            Jwt jwt = jwtDecoder.decode(token);

            // Extract email from the decoded JWT
            String email = jwt.getSubject();  // Assuming the email is the subject in the JWT

            // Return the email extracted from the JWT
            return "From JWT token, the email is " + email;

        } catch (Exception e) {
            // If decoding fails, return an invalid token message
            return "JWT is invalid";
        }
    }
}
