package com.example.server.Utils;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.example.server.Service.CustomeUserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends org.springframework.web.filter.OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private CustomeUserService customService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if (path.startsWith("/user/add") || path.startsWith("/user/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = null, username = null;
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtService.extractUsername(token);
        }
        if (token == null && request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("token".equals(c.getName())) {
                    token = c.getValue();
                    username = jwtService.extractUsername(token);
                    break;
                }
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = customService.loadUserByUsername(username);
            if (jwtService.validateToken(token, userDetails.getUsername())) {
                var authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}