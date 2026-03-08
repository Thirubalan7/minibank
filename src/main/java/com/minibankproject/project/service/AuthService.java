package com.minibankproject.project.service;

import com.minibankproject.project.dto.LoginRequest;
import com.minibankproject.project.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Value("${session.timeout:300}")
    private long sessionTimeoutSeconds;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String login(LoginRequest request) {


        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );


        UserDetails userDetails = (UserDetails) authentication.getPrincipal();


        String role = userDetails.getAuthorities()
                .iterator()
                .next()
                .getAuthority();


        String sessionId = UUID.randomUUID().toString();

        try {
            redisTemplate.opsForValue().set(
                    userDetails.getUsername(),
                    sessionId,
                    Duration.ofSeconds(sessionTimeoutSeconds)
            );
        } catch (Exception ex) {
            log.warn("Redis unavailable; skipping session storage", ex);
        }

        // generate JWT
        return jwtUtil.generateToken(
                userDetails.getUsername(),
                role,
                sessionId
        );
    }
}