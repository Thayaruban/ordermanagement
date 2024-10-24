package com.zerobeta.assignment.ordermanagement.service;

import com.zerobeta.assignment.ordermanagement.entity.User;
import com.zerobeta.assignment.ordermanagement.dto.SignUpRequestDTO;
import com.zerobeta.assignment.ordermanagement.exception.EntityNotFoundException;
import com.zerobeta.assignment.ordermanagement.exception.UserAlreadyExistsException;
import com.zerobeta.assignment.ordermanagement.repository.UserRepository;
import com.zerobeta.assignment.ordermanagement.entity.UserInfoDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignup_UserAlreadyExists() {

        SignUpRequestDTO signUpRequest = new SignUpRequestDTO("user@gmail.com", "password", "Ruban", "Thaya");
        when(userRepository.findByEmail(signUpRequest.getEmail())).thenReturn(Optional.of(new User()));

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.signup(signUpRequest);
        });

        assertEquals("Email Already in Use: user@gmail.com", exception.getMessage());
    }

    @Test
    void testSignup_Success() {

        SignUpRequestDTO signUpRequest = new SignUpRequestDTO("user@gmail.com", "password", "Ruban", "Thaya");
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setPassword("encodedPassword");
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());

        when(userRepository.findByEmail(signUpRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.signup(signUpRequest);

        verify(userRepository, times(1)).save(any(User.class));
        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        User user = new User("user@gmail.com", "password", "Ruban", "Thaya");
        when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.of(user));

        UserInfoDetails userInfoDetails = (UserInfoDetails) userService.loadUserByUsername("user@gmail.com");

        assertNotNull(userInfoDetails);
        assertEquals("user@gmail.com", userInfoDetails.getUsername());
        assertEquals("password", userInfoDetails.getPassword());
    }

    // Test for loadUserByUsername method (User not found case)
    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.loadUserByUsername("nonexistent@example.com");
        });
    }
}
