package ru.practicum.shareit.booking;

import java.util.Optional;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<BookingState> from(String stringState) {
        for (BookingState value : BookingState.values()) {
            if (value.name().equals(stringState.toUpperCase())) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
