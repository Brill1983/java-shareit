package ru.practicum.shareit.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BookingDtoIn> {

    @Override
    public boolean isValid(BookingDtoIn booking, ConstraintValidatorContext context) {
        if (booking == null || booking.getStart() == null || booking.getEnd() == null) {
            return true;
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            return false;
        }
        if (booking.getStart().isEqual(booking.getEnd())) {
            return false;
        }
        return true;
    }
}
