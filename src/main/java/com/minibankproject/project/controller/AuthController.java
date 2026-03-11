package com.minibankproject.project.controller;

import com.minibankproject.project.dto.LoginRequest;
import com.minibankproject.project.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        try {

            String token = authService.login(request);

            // store data in session
            session.setAttribute("userEmail", request.getEmail());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "sessionId", session.getId()
            ));

        } catch (AuthenticationException ex) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));

        }
    }
}