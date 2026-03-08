package com.minibankproject.project.security;

import com.minibankproject.project.service.CustomUserDetailsService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired(required = false)
    private RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        if (path != null && (path.startsWith("/auth/") || path.equals("/error") || path.startsWith("/swagger-ui") || path.startsWith("/api-docs"))) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            String username;
            String tokenSessionId;
            try {
                username = jwtUtil.extractUsername(token);
                tokenSessionId = jwtUtil.extractSessionId(token);
                if (jwtUtil.isTokenExpired(token)) {
                    username = null;
                }
            } catch (Exception ex) {
                username = null;
                tokenSessionId = null;
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Enforce single active session (if Redis is available)
                if (redisTemplate != null) {
                    try {
                        String activeSessionId = redisTemplate.opsForValue().get(username);
                        if (activeSessionId == null || tokenSessionId == null || !activeSessionId.equals(tokenSessionId)) {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            return;
                        }
                    } catch (Exception ignored) {
                        // If Redis is down, don't block requests (best-effort enforcement)
                    }
                }

                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}