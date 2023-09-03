package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
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
