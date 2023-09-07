package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.service.StartBeforeEnd;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@StartBeforeEnd
public class BookingDtoIn {

    private Long id = 0L;

    @NotNull(message = "Нужно указать дату возврата вещи")
    @FutureOrPresent(message = "Нельзя указывать дату из прошлого")
    private LocalDateTime start;

    @NotNull(message = "Нужно указать дату возврата вещи")
    @FutureOrPresent(message = "Нельзя указывать дату из прошлого для возврата вещи")
    private LocalDateTime end;

    @NotNull(message = "Нужно указать, какую вещь хотите арендовать")
    private Long itemId;

    private Status status = Status.WAITING;

}
