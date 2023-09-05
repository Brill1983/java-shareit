package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoDated {

    private long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingDtoForItem lastBooking;

    private BookingDtoForItem nextBooking;

    private List<CommentDto> comments = new ArrayList<>();
}
