package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingDtoIn {

    private Long id = 0L;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Status status = Status.WAITING;

}
