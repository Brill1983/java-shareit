package ru.practicum.shareit.exceptions;

import org.hibernate.JDBCException;
import org.springframework.dao.DataIntegrityViolationException;

public class EmailExistException extends DataIntegrityViolationException/*RuntimeException*/ {

//    public String message = "Пользователь с такой почтой уже зарегистрирован";
    public EmailExistException(String message) {
        super(message);
    }
}
