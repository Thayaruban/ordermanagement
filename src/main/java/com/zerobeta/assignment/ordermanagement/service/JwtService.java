package com.zerobeta.assignment.ordermanagement.service;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service class for managing JSON Web Tokens (JWT).
 * This class provides methods for generating, validating, and extracting claims from JWTs.
 */
@Component
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret.key}")
    private String secretKey; // The secret key used for signing the JWTs.

    @Value("${jwt.expiration.time.ms:600000}") // Default expiration time set to 10 minutes if not provided.
    private long jwtExpirationTime;

    /**
     * Generates a JWT for the specified email.
     *
     * @param email The email for which the JWT is generated.
     * @return A JWT as a String.
     */
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        logger.info("Generating token for email: {}", email);
        return createToken(claims, email);
    }

    /**
     * Creates a JWT with the specified claims and subject.
     *
     * @param claims The claims to be included in the JWT.
     * @param subject The subject (email) for the JWT.
     * @return A JWT as a String.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        logger.info("Creating JWT token for subject: {}", subject);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Sign the token using the secret key
                .compact();
    }

    /**
     * Extracts the email from the given JWT.
     *
     * @param token The JWT from which to extract the email.
     * @return The email as a String.
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the JWT.
     *
     * @param token The JWT from which to extract the claim.
     * @param claimsResolver A function to extract the desired claim.
     * @param <T> The type of the claim.
     * @return The extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the JWT, validating the signature in the process.
     *
     * @param token The JWT from which to extract claims.
     * @return The claims contained in the JWT.
     * @throws JwtException if the token is invalid.
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSigningKey()) // Use the signing key to validate the token
                    .parseClaimsJws(token) // Parse and validate the JWT
                    .getBody();
        } catch (JwtException e) { // Handle all JWT exceptions
            logger.error("Failed to extract claims from token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Validates the JWT against the provided username.
     *
     * This method attempts to extract the email from the JWT and checks if it matches the provided username.
     * If any JwtException occurs during the extraction or validation, it logs the error and returns true.
     *
     * @param token The JWT to validate.
     * @param username The username (email) to validate against.
     * @return True if the token is valid or if an exception occurs; otherwise, false.
     */
    public Boolean validateToken(String token, String username) {
        try {
            final String extractedEmail = extractEmail(token);
            boolean isValid = (extractedEmail.equals(username) && !isTokenExpired(token));
            logger.info("Validating token for user: {}, token valid: {}", extractedEmail, isValid);
            return isValid;
        } catch (JwtException e) {
            logger.error("JWT Exception occurred while validating token: {}", e.getMessage());
            return true;
        }
    }



    /**
     * Checks if the JWT is expired.
     *
     * @param token The JWT to check.
     * @return True if the token is expired; otherwise, false.
     */
    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (JwtException e) {
            logger.error("JWT Exception occurred while checking if token is expired: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Extracts the expiration date from the JWT.
     *
     * @param token The JWT from which to extract the expiration date.
     * @return The expiration date as a Date object.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Retrieves the signing key used for JWT signing.
     *
     * @return The signing key as a Key object.
     */
    private Key getSigningKey() {
        // Convert the secret key from a string to a Key object
        return new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }
}
