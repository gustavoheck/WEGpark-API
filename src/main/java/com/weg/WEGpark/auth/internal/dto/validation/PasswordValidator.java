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
                customMessage(context, "The password must contain at least one uppercase letter");
            }
            else if (Character.isLowerCase(ch)) {
                hasLowercase = true;
                customMessage(context, "The password must contain at least one lowercase letter");
            }
            else if (Character.isDigit(ch)) {
                hasNumber = true;
                customMessage(context, "The password must contain at least one number");
            }
            else if (specialCharacters.contains(String.valueOf(ch))) {
                hasSpecialChar = true;
                customMessage(context, "The password must contain at least one special character letter");
            }
        }

        return hasUppercase && hasLowercase && hasNumber && hasSpecialChar && password.length() >= 8;
    }

    private void customMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }

}