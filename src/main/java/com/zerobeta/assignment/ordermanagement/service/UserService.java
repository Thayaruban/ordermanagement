package com.zerobeta.assignment.ordermanagement.service;

import com.zerobeta.assignment.ordermanagement.entity.User;
import com.zerobeta.assignment.ordermanagement.entity.UserInfoDetails;
import com.zerobeta.assignment.ordermanagement.dto.SignUpRequestDTO;
import com.zerobeta.assignment.ordermanagement.exception.UserAlreadyExistsException;
import com.zerobeta.assignment.ordermanagement.exception.EntityNotFoundException;
import com.zerobeta.assignment.ordermanagement.repository.UserRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class that handles user-related operations.
 * This class provides methods for user registration, loading user details, and
 * managing user-related exceptions.
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registers a new user based on the provided sign-up request data.
     *
     * @param clientRequest The sign-up request data transfer object containing user details.
     * @throws UserAlreadyExistsException if a user with the same email already exists.
     */
    public void signup(SignUpRequestDTO clientRequest) {
        if (userRepository.findByEmail(clientRequest.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email Already in Use: " + clientRequest.getEmail());
        }

        User user = new User();
        BeanUtils.copyProperties(clientRequest, user);

        // Set the password after encoding
        user.setPassword(passwordEncoder.encode(clientRequest.getPassword()));

        userRepository.save(user);
    }

    /**
     * Loads user details by username (email).
     *
     * @param username The email of the user to be loaded.
     * @return UserDetails object representing the user.
     * @throws UsernameNotFoundException if the user with the specified email is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);

        // Convert User entity to UserDetails
        return user.map(UserInfoDetails::new)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found " + username));
    }
}
