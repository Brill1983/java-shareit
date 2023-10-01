package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookingMapperTest {

    private Booking booking;
    private BookingDtoIn bookingDtoIn;
    private Item item;
    private ItemDto itemDto;
    private User user;
    private UserDto userDto;
    private Request request;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "Иван Иванович", "ii@mail.ru");
        userDto = new UserDto(1L, "Иван Иванович", "ii@mail.ru");
        request = new Request(1L, "Request 1", user, LocalDateTime.now());
        item = new Item(1L, "Вещь 1", "Описание вещи 1", true, user, request);
        itemDto = new ItemDto(1L, "Вещь 1", "Описание вещи 1", true, request.getId());
        booking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(5), item,
                user, Status.APPROVED);
        bookingDtoIn = new BookingDtoIn(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(5),
                item.getId(), Status.APPROVED);
    }

    @Test
    void toBookingDto() {
        BookingDtoOut bookingDto = BookingMapper.toBookingDto(booking);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
        assertNotNull(bookingDto.getStart());
        assertNotNull(bookingDto.getEnd());
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        assertEquals(booking.getItem().getName(), bookingDto.getItem().getName());
        assertEquals(booking.getItem().getDescription(), bookingDto.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), bookingDto.getItem().getAvailable());
        assertEquals(booking.getItem().getRequest().getId(), bookingDto.getItem().getRequestId());
        assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
        assertEquals(booking.getBooker().getName(), bookingDto.getBooker().getName());
        assertEquals(booking.getBooker().getEmail(), bookingDto.getBooker().getEmail());
    }

    @Test
    void toItemBookingDto() {
        BookingDtoForItem bookingDto = BookingMapper.toItemBookingDto(booking);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
        assertNotNull(bookingDto.getStart());
        assertNotNull(bookingDto.getEnd());
        assertEquals(booking.getBooker().getId(), bookingDto.getBookerId());
    }

    @Test
    void toBooking() {
        Booking mapperBooking = BookingMapper.toBooking(bookingDtoIn, user, item);

        assertEquals(booking.getId(), mapperBooking.getId());
        assertEquals(booking.getStatus(), mapperBooking.getStatus());
        assertNotNull(mapperBooking.getStart());
        assertNotNull(mapperBooking.getEnd());
        assertEquals(booking.getItem().getId(), mapperBooking.getItem().getId());
        assertEquals(booking.getItem().getName(), mapperBooking.getItem().getName());
        assertEquals(booking.getItem().getDescription(), mapperBooking.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), mapperBooking.getItem().getAvailable());
        assertEquals(booking.getItem().getRequest().getId(), mapperBooking.getItem().getRequest().getId());
        assertEquals(booking.getBooker().getId(), mapperBooking.getBooker().getId());
        assertEquals(booking.getBooker().getName(), mapperBooking.getBooker().getName());
        assertEquals(booking.getBooker().getEmail(), mapperBooking.getBooker().getEmail());

    }
}