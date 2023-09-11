package ru.practicum.shareit.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, StartEndChecker> {

    @Override
    public boolean isValid(StartEndChecker checker, ConstraintValidatorContext context) {
        if (checker == null || checker.getStart() == null || checker.getEnd() == null) {
            return true;
        }
        return checker.getEnd().isAfter(checker.getStart()) || checker.getStart().isEqual(checker.getEnd());
    }
}
