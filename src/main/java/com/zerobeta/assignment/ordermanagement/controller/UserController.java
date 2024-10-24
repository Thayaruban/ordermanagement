package com.zerobeta.assignment.ordermanagement.controller;

import com.zerobeta.assignment.ordermanagement.dto.SignInRequestDTO;
import com.zerobeta.assignment.ordermanagement.dto.SignUpRequestDTO;
import com.zerobeta.assignment.ordermanagement.dto.APIResponseDTO;
import com.zerobeta.assignment.ordermanagement.exception.EntityNotFoundException;
import com.zerobeta.assignment.ordermanagement.service.UserService;
import com.zerobeta.assignment.ordermanagement.service.JwtService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing user operations in the order management system.
 * This class handles requests related to user registration and authentication.
 */
@RestController
@RequestMapping("/order-management/user")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserController(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registers a new user in the system.
     *
     * @param signUpRequest The details of the user to be registered.
     * @return ResponseEntity indicating success or failure of the registration.
     */
    @PostMapping("/signup")
    public ResponseEntity<APIResponseDTO<String>> signup(@Valid @RequestBody SignUpRequestDTO signUpRequest) {
        userService.signup(signUpRequest);
        APIResponseDTO<String> response = new APIResponseDTO<>(true, "User Registered Successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param signInRequest The credentials of the user trying to sign in.
     * @return ResponseEntity containing the JWT token if authentication is successful.
     */
    @PostMapping("/signin")
    public ResponseEntity<APIResponseDTO<String>> signIn(@Valid @RequestBody SignInRequestDTO signInRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getEmail(),
                        signInRequest.getPassword()));

        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(signInRequest.getEmail());
            APIResponseDTO<String> response = new APIResponseDTO<>(true, "Authentication Successful", token);
            return ResponseEntity.ok(response);
        } else {
            throw new EntityNotFoundException("Invalid User Request!");
        }
    }
}
