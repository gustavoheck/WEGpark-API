package com.weg.WEGpark.auth.internal.dto.validation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return true;
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasNumber = false;
        boolean hasSpecialChar = false;

        String specialCharacters = "!@#$%^&*(),.?\"{}|<>";

        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(ch)) {
                hasLowercase = true;
            } else if (Character.isDigit(ch)) {
                hasNumber = true;
            } else if (specialCharacters.contains(String.valueOf(ch))) {
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