package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookingStateTest {

    @Test
    void from() {
        String stringState = "aLl";
        Optional<BookingState> bookingState = BookingState.from(stringState);
        assertTrue(bookingState.isPresent());
        assertEquals(BookingState.ALL, bookingState.get());

        stringState = "cuRrEnT";
        bookingState = BookingState.from(stringState);
        assertTrue(bookingState.isPresent());
        assertEquals(BookingState.CURRENT, bookingState.get());

        stringState = "PAST";
        bookingState = BookingState.from(stringState);
        assertTrue(bookingState.isPresent());
        assertEquals(BookingState.PAST, bookingState.get());

        stringState = "future";
        bookingState = BookingState.from(stringState);
        assertTrue(bookingState.isPresent());
        assertEquals(BookingState.FUTURE, bookingState.get());

        stringState = "WaiTInG";
        bookingState = BookingState.from(stringState);
        assertTrue(bookingState.isPresent());
        assertEquals(BookingState.WAITING, bookingState.get());

        stringState = "rejecteD";
        bookingState = BookingState.from(stringState);
        assertTrue(bookingState.isPresent());
        assertEquals(BookingState.REJECTED, bookingState.get());
    }

    @Test
    void values() {
        BookingState[] bookingStates = BookingState.values();

        assertEquals(6, bookingStates.length);
    }

    @Test
    void valueOf() {
        String stringState = "REJECTED";
        BookingState bookingState = BookingState.valueOf(stringState);

        assertEquals(BookingState.REJECTED, bookingState);
    }
}