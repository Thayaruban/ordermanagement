package com.zerobeta.assignment.ordermanagement.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isBlank()) {
            setMessage(context, "Password cannot be blank");
            return false;
        }

        if (password.length() < 8) {
            setMessage(context, "Password must be at least 8 characters long");
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            setMessage(context, "Password must contain at least one uppercase letter");
            return false;
        }

        if (!password.matches(".*[a-z].*")) {
            setMessage(context, "Password must contain at least one lowercase letter");
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            setMessage(context, "Password must contain at least one digit");
            return false;
        }

        if (!password.matches(".*[!@#$%^&*].*")) {
            setMessage(context, "Password must contain at least one special character (e.g., !@#$%^&*)");
            return false;
        }

        return true;
    }

    private void setMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
