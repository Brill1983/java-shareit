package ru.practicum.shareit.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BookingDto> {

    @Override
    public boolean isValid(BookingDto booking, ConstraintValidatorContext context) {
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
