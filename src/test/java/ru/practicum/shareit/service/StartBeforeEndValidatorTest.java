package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDtoIn;

import javax.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class StartBeforeEndValidatorTest {

    @Test
    void isValid() {
        StartBeforeEndValidator validator = new StartBeforeEndValidator();
        StartEndChecker checker = new BookingDtoIn(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(5), 1L, Status.APPROVED);


    }

//    @Override
//    public boolean isValid(StartEndChecker checker, ConstraintValidatorContext context) {
//        if (checker == null || checker.getStart() == null || checker.getEnd() == null) {
//            return true;
//        }
//        if (checker.getEnd().isBefore(checker.getStart())) {
//            return false;
//        }
//        if (checker.getStart().isEqual(checker.getEnd())) {
//            return false;
//        }
//        return true;
//    }
}