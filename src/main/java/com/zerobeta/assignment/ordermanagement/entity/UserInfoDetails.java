package com.zerobeta.assignment.ordermanagement.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * UserInfoDetails is a class that implements the UserDetails interface from Spring Security.
 * It is used to represent a user in the Spring Security context and integrates with the User class.
 * This class provides the necessary user information required for authentication and authorization.
 */
public class UserInfoDetails implements UserDetails {

    private final String email;  // Use email as username
    private final String password;

    /**
     * Constructor that initializes UserInfoDetails with a User object.
     *
     * @param userInfo The User object containing user information.
     */
    public UserInfoDetails(User userInfo) {
        this.email = userInfo.getEmail();  // Set email as username
        this.password = userInfo.getPassword();
    }

    /**
     * Returns the authorities granted to the user.
     * In this implementation, no specific authorities are provided.
     *
     * @return An empty collection of GrantedAuthority.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    /**
     * Returns the password of the user.
     *
     * @return The password of the user.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the username of the user.
     * In this case, the username is represented by the user's email.
     *
     * @return The email of the user as the username.
     */
    @Override
    public String getUsername() {
        return email;  // Return email as username
    }

    /**
     * Indicates whether the user's account has expired.
     * This implementation always returns false, indicating the account is valid.
     *
     * @return false as the account is not expired.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or disabled.
     * This implementation always returns true, indicating the account is not locked.
     *
     * @return true as the account is not locked.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) have expired.
     * This implementation always returns true, indicating the credentials are valid.
     *
     * @return true as the credentials are not expired.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or not.
     * This implementation always returns true, indicating the user is enabled.
     *
     * @return true as the user is enabled.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
