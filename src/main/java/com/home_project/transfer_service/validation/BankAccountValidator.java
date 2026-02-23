package com.home_project.transfer_service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class BankAccountValidator implements ConstraintValidator<ValidBankAccount, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return value.matches("[A-Z]{2}[0-9]{22}");
    }
}
