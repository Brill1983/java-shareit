package ru.practicum.shareit.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BookingDtoIn> {

    @Override
    public boolean isValid(BookingDtoIn bookingDtoIn, ConstraintValidatorContext context) {
        if (bookingDtoIn == null || bookingDtoIn.getStart() == null || bookingDtoIn.getEnd() == null) {
            return true;
        }
        return bookingDtoIn.getEnd().isAfter(bookingDtoIn.getStart()) || bookingDtoIn.getStart().isEqual(bookingDtoIn.getEnd());
    }
}
