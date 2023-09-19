package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoDated;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void toItemDto() {
        User user = new User(1L, "Иван Иванович", "ii@mail.ru");
        Request request = new Request(1L, "Request 1", user, LocalDateTime.now());
        Item item = new Item(1L, "Вещь 1", "Описание вещи 1", true, user, request);

        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getRequest().getId(), itemDto.getRequestId());
    }

//    public static ItemDto toItemDto(Item item) {
//        return new ItemDto(
//                item.getId(),
//                item.getName(),
//                item.getDescription(),
//                item.getAvailable(),
//                item.getRequest() != null ? item.getRequest().getId() : null
//        );
//    }

    @Test
    void testToItemDto() {
        BookingDtoForItem bookingLastDto = new BookingDtoForItem(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(5), 1L, Status.APPROVED);
        BookingDtoForItem bookingNextDto = new BookingDtoForItem(2L, LocalDateTime.now().plusHours(12), LocalDateTime.now().plusDays(1), 1L, Status.APPROVED);
        User user = new User(1L, "Иван Иванович", "ii@mail.ru");
        Request request = new Request(1L, "Request 1", user, LocalDateTime.now());
        Item item = new Item(1L, "Вещь 1", "Описание вещи 1", true, user, request);
        ItemDto itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), request.getId());
        CommentDto comment = new CommentDto(1L, "Коммент 1", itemDto, user.getName(), LocalDateTime.now());

        ItemDtoDated itemFromMapper = ItemMapper.toItemDto(item, bookingLastDto, bookingNextDto, List.of(comment));

        assertEquals(item.getId(), itemFromMapper.getId());
        assertEquals(item.getName(), itemFromMapper.getName());
        assertEquals(item.getDescription(), itemFromMapper.getDescription());
        assertEquals(item.getAvailable(), itemFromMapper.getAvailable());
        assertEquals(bookingLastDto.getId(), itemFromMapper.getLastBooking().getId());
        assertEquals(bookingLastDto.getBookerId(), itemFromMapper.getLastBooking().getBookerId());
        assertEquals(bookingLastDto.getStatus(), itemFromMapper.getLastBooking().getStatus());
        assertNotNull(itemFromMapper.getLastBooking().getStart());

        assertEquals(bookingNextDto.getId(), itemFromMapper.getNextBooking().getId());
        assertEquals(bookingNextDto.getBookerId(), itemFromMapper.getNextBooking().getBookerId());
        assertEquals(bookingNextDto.getStatus(), itemFromMapper.getNextBooking().getStatus());
        assertNotNull(itemFromMapper.getNextBooking().getStart());

        assertEquals(1, itemFromMapper.getComments().size());
    }

//    public static ItemDtoDated toItemDto(Item item, BookingDtoForItem lastBooking, BookingDtoForItem nextBooking, List<CommentDto> comments) {
//        return new ItemDtoDated(
//                item.getId(),
//                item.getName(),
//                item.getDescription(),
//                item.getAvailable(),
//                lastBooking,
//                nextBooking,
//                comments
//        );
//    }

    @Test
    void toItem() {
    }

    @Test
    void testToItem() {
    }
}