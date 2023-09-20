package ru.practicum.shareit.service;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, StartEndChecker> {

    @Override
    public boolean isValid(StartEndChecker checker, ConstraintValidatorContext context) {
        if (checker == null || checker.getStart() == null || checker.getEnd() == null) {
            return true;
        }
        if (checker.getEnd().isBefore(checker.getStart())) {
            return false;
        }
        if (checker.getStart().isEqual(checker.getEnd())) {
            return false;
        }
        return true;
    }
}
