package com.zerobeta.assignment.ordermanagement.util;



import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PasswordValidatorTest {

    private PasswordValidator passwordValidator;
    private ConstraintValidatorContext context;
    private ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    public void setUp() {
        passwordValidator = new PasswordValidator();
        context = mock(ConstraintValidatorContext.class);
        violationBuilder = mock(ConstraintViolationBuilder.class);

        // Mocking the behavior of context.buildConstraintViolationWithTemplate to return a mock ConstraintViolationBuilder
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        // Ensure that addConstraintViolation() does not cause a null pointer exception
        when(violationBuilder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    public void testValidPassword() {
        assertTrue(passwordValidator.isValid("Valid123!", context));
    }

    @Test
    public void testBlankPassword() {
        assertFalse(passwordValidator.isValid("", context));
        verify(context).buildConstraintViolationWithTemplate("Password cannot be blank");
    }

    @Test
    public void testShortPassword() {
        assertFalse(passwordValidator.isValid("Short1!", context));
        verify(context).buildConstraintViolationWithTemplate("Password must be at least 8 characters long");
    }

    @Test
    public void testPasswordWithoutUppercase() {
        assertFalse(passwordValidator.isValid("lowercase1!", context));
        verify(context).buildConstraintViolationWithTemplate("Password must contain at least one uppercase letter");
    }

    @Test
    public void testPasswordWithoutLowercase() {
        assertFalse(passwordValidator.isValid("UPPERCASE1!", context));
        verify(context).buildConstraintViolationWithTemplate("Password must contain at least one lowercase letter");
    }

    @Test
    public void testPasswordWithoutDigit() {
        assertFalse(passwordValidator.isValid("NoDigit!", context));
        verify(context).buildConstraintViolationWithTemplate("Password must contain at least one digit");
    }

    @Test
    public void testPasswordWithoutSpecialCharacter() {
        assertFalse(passwordValidator.isValid("NoSpecialChar1", context));
        verify(context).buildConstraintViolationWithTemplate("Password must contain at least one special character (e.g., !@#$%^&*)");
    }
}
