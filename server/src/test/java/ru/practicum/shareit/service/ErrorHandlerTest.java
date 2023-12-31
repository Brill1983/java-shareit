package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exceptions.ElementNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorHandlerTest {

    private ErrorHandler errorHandler;

    @BeforeEach
    void beforeEach() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void handleEmailExistExc() {
        String message = "Пользователь с такой почтой уже зарегистрирован";
        DataIntegrityViolationException exc = new DataIntegrityViolationException("Причина");

        ErrorResponse errorResponse = errorHandler.handleEmailExistExc(exc);

        assertEquals(message, errorResponse.getError());
    }

    @Test
    void handleNotFoundExc() {
        String message = "Такого элемента не зарегистрировано";
        ElementNotFoundException exc = new ElementNotFoundException(message);

        ErrorResponse errorResponse = errorHandler.handleNotFoundExc(exc);

        assertEquals(message, errorResponse.getError());
    }

    @Test
    void handleOtherExc() {
        String message = "Ошибка сервера";
        Throwable exc = new Throwable(message);

        ErrorResponse errorResponse = errorHandler.handleOtherExc(exc);

        assertNotNull(errorResponse.getError());
    }
}