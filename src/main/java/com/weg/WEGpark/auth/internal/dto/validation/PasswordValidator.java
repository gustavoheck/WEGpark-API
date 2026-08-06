package com.weg.WEGpark.auth.internal.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static final String SPECIAL_CHARACTERS = "!@#$%^&*(),.?\"{}|<>";

    private boolean nullable;

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        this.nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isBlank() && !nullable) {
            context.disableDefaultConstraintViolation();
            addViolation(context, "The password can not be blank or null");
            return false;
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasNumber = false;
        boolean hasSpecialChar = false;

        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(ch)) {
                hasLowercase = true;
            } else if (Character.isDigit(ch)) {
                hasNumber = true;
            } else if (SPECIAL_CHARACTERS.indexOf(ch) >= 0) {
                hasSpecialChar = true;
            }
        }

        boolean isValid = true;

        context.disableDefaultConstraintViolation();

        if (password.length() < 8) {
            addViolation(context, "The password must be at least 8 characters long");
            isValid = false;
        }
        if (!hasUppercase) {
            addViolation(context, "The password must contain at least one uppercase letter");
            isValid = false;
        }
        if (!hasLowercase) {
            addViolation(context, "The password must contain at least one lowercase letter");
            isValid = false;
        }
        if (!hasNumber) {
            addViolation(context, "The password must contain at least one number");
            isValid = false;
        }
        if (!hasSpecialChar) {
            addViolation(context, "The password must contain at least one special character");
            isValid = false;
        }

        return isValid;
    }

    private void addViolation(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}