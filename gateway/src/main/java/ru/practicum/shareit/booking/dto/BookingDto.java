package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.service.StartBeforeEnd;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEnd
public class BookingDto {

    private Long id = 0L;

    @NotNull(message = "Нужно указать дату возврата вещи")
    @FutureOrPresent(message = "Нельзя указывать дату из прошлого")
    private LocalDateTime start;

    @NotNull(message = "Нужно указать дату возврата вещи")
    @Future(message = "Нельзя указывать дату из прошлого для возврата вещи")
    private LocalDateTime end;

    @NotNull(message = "Нужно указать, какую вещь хотите арендовать")
    private Long itemId;

    private Status status = Status.WAITING;
}
