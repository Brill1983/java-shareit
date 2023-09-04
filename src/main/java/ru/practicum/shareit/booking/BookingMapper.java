package ru.practicum.shareit.booking;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor
public class BookingMapper {

    public static BookingDtoOut toBookingDto(Booking booking) {
        if(booking == null) {
            return null;
        }
        return new BookingDtoOut(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toItemDto(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public static BookingDtoForItem toItemBookingDto(Booking booking) {
        if(booking == null) {
            return null;
        }
        return new BookingDtoForItem(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingDtoIn bookingDto, User user, Item item) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                bookingDto.getStatus()
        );
    }
}
