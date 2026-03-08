package com.minibankproject.project.service;

import com.minibankproject.project.entity.UserEntity;
import com.minibankproject.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        String normalizedEmail = email == null ? null : email.trim().toLowerCase();

        UserEntity user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String storedPassword = user.getPassword() == null ? "" : user.getPassword().trim();
        if (!storedPassword.startsWith("{") && !storedPassword.startsWith("$2a$") && !storedPassword.startsWith("$2b$") && !storedPassword.startsWith("$2y$")) {
            storedPassword = "{noop}" + storedPassword;
        }

        return new User(
                user.getEmail(),
                storedPassword,
                List.of(new SimpleGrantedAuthority(user.getRole().getRoleName().name()))
        );
    }
}