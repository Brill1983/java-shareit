package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    String error = "Error";

    @Test
    void getError() {
        ErrorResponse errorResponse = new ErrorResponse(error);

        String errorFromResponse = errorResponse.getError();

        assertEquals(error, errorFromResponse);
    }
}