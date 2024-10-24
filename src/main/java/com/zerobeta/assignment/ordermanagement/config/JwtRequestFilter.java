package com.zerobeta.assignment.ordermanagement.config;


import com.zerobeta.assignment.ordermanagement.service.JwtService;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private JwtService jwtUtil;

    @Autowired
    private CustomAuthenticationEntryPoint unauthorizedHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Extract token
            try {
                email = jwtUtil.extractEmail(jwt);
            } catch (JwtException e) {
                log.error("JWT Token error: {}", e.getMessage());
                unauthorizedHandler.commence(request, response, new AuthenticationException("Invalid JWT Token") {});
                return;
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (Boolean.FALSE.equals(jwtUtil.validateToken(jwt, email))) {
                log.warn("JWT Token validation failed for user: {}", email);
                unauthorizedHandler.commence(request, response,  new AuthenticationException("JWT Token is not valid"){});
                return;
            } else {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                log.info("JWT Token Validated, Setting Authentication For User: {}", email);
            }
        }

        chain.doFilter(request, response);
    }
}
