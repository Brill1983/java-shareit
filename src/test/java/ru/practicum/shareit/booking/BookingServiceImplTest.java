package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BadParameterException;
import ru.practicum.shareit.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.service.ValidationService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private ValidationService validationService;
    private BookingServiceImpl bookingService;
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;

    Item item;
    User user;
    User user2;
    Booking booking;
    BookingDtoIn bookingDtoIn;


    @BeforeEach
    void beforeEach() {
        user = new User(1L, "Иван Иванович", "ii@mail.ru");
        user2 = new User(2L, "Петр Петрович", "pp@mail.ru");
        item = new Item(1L, "Вещь 1", "Описание вещи 1", true, user, null);
        booking = new Booking(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1), item, user2, Status.APPROVED);
        bookingDtoIn = new BookingDtoIn(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1), item.getId(), Status.APPROVED);

        validationService = mock(ValidationService.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);

        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, validationService);
    }

    @Test
    void saveBooking() {
        long userId = 2L;
        when(validationService.checkUser(anyLong()))
                .thenReturn(user2);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingDtoOut bookingDtoOut = bookingService.saveBooking(userId, bookingDtoIn);

        assertThat(bookingDtoOut.getId(), equalTo(bookingDtoIn.getId()));
        assertThat(bookingDtoOut.getStatus(), equalTo(bookingDtoIn.getStatus()));
        assertThat(bookingDtoOut.getBooker().getId(), equalTo(userId));
        assertThat(bookingDtoOut.getItem().getId(), equalTo(bookingDtoIn.getItemId()));
        assertThat(bookingDtoOut.getStart(), notNullValue());
        assertThat(bookingDtoOut.getEnd(), notNullValue());

        verify(validationService, times(1))
                .checkUser(anyLong());
        verify(itemRepository, times(1))
                .findById(anyLong());
        verify(bookingRepository, times(1))
                .save(any());
    }

    @Test
    void saveBookingWithWrongUserId() {
        long userId = 3L;
        when(validationService.checkUser(anyLong()))
                .thenThrow(new UserNotFoundException("Пользователь с таким ID не зарегистрировано"));
        try {
            bookingService.saveBooking(userId, bookingDtoIn);
        } catch (UserNotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с таким ID не зарегистрировано"));
        }

        verify(validationService, times(1))
                .checkUser(anyLong());
        verify(itemRepository, never())
                .findById(anyLong());
        verify(bookingRepository, never())
                .save(any());
    }

    @Test
    void saveBookingWithWrongItemId() {
        long userId = 2L;
        when(validationService.checkUser(anyLong()))
                .thenReturn(user2);
        when(itemRepository.findById(anyLong()))
                .thenThrow(new ItemNotFoundException("Предмета с ID " + bookingDtoIn.getItemId() + " не зарегистрировано"));
        try {
            bookingService.saveBooking(userId, bookingDtoIn);
        } catch (ItemNotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Предмета с ID " + bookingDtoIn.getItemId() + " не зарегистрировано"));
        }

        verify(validationService, times(1))
                .checkUser(anyLong());
        verify(itemRepository, times(1))
                .findById(anyLong());
        verify(bookingRepository, never())
                .save(any());
    }

    @Test
    void saveBookingWithUnavailableItem() {
        long userId = 2L;
        item.setAvailable(false);
        when(validationService.checkUser(anyLong()))
                .thenReturn(user2);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        try {
            bookingService.saveBooking(userId, bookingDtoIn);
        } catch (BadParameterException thrown) {
            assertThat(thrown.getMessage(), equalTo("У выбранной для аренды вещи статус: недоступна"));
        }

        verify(validationService, times(1))
                .checkUser(anyLong());
        verify(itemRepository, times(1))
                .findById(anyLong());
        verify(bookingRepository, never())
                .save(any());
    }

    @Test
    void saveBookingFromItemOwner() {
        long userId = 1L;
        when(validationService.checkUser(anyLong()))
                .thenReturn(user);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        try {
            bookingService.saveBooking(userId, bookingDtoIn);
        } catch (ItemNotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Где-то ошибка: запрос аренды отправлен от владельца вещи"));
        }

        verify(validationService, times(1))
                .checkUser(anyLong());
        verify(itemRepository, times(1))
                .findById(anyLong());
        verify(bookingRepository, never())
                .save(any());
    }

    @Test
    void bookingApprove() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;
        booking.setStatus(Status.WAITING);
        when(validationService.checkUser(anyLong()))
                .thenReturn(user);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingDtoOut bookingDtoOut = bookingService.bookingApprove(userId, bookingId, approved);

        assertThat(bookingDtoOut.getId(), equalTo(bookingId));
        assertThat(bookingDtoOut.getStatus(), equalTo(Status.APPROVED));
        assertThat(bookingDtoOut.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(bookingDtoOut.getStart(), notNullValue());
        assertThat(bookingDtoOut.getEnd(), notNullValue());

        verify(validationService, times(1))
                .checkUser(anyLong());
        verify(bookingRepository, times(1))
                .findById(anyLong());
        verify(bookingRepository, times(1))
                .save(any());
    }

    @Test
    void bookingApproveWithWrongUserId() {
        long userId = 3L;
        long bookingId = 1L;
        boolean approved = true;
        when(validationService.checkUser(anyLong()))
                .thenThrow(new UserNotFoundException("Пользователь с таким ID не зарегистрировано"));
        try {
            bookingService.bookingApprove(userId, bookingId, approved);
        } catch (UserNotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с таким ID не зарегистрировано"));
        }

        verify(validationService, times(1))
                .checkUser(anyLong());
        verify(bookingRepository, never())
                .findById(anyLong());
        verify(bookingRepository, never())
                .save(any());
    }

    @Test
    void bookingApproveNotFromItemOwner() {
        long userId = 2L;
        long bookingId = 1L;
        boolean approved = true;
        when(validationService.checkUser(anyLong()))
                .thenReturn(user);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        try {
            bookingService.bookingApprove(userId, bookingId, approved);
        } catch (BookingNotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь ID " + userId + " не является владельцем вещи с ID " + booking.getItem().getId() + " и не может менять одобрить/отклонить запрос на аренду этой вещи"));
        }

        verify(validationService, times(1))
                .checkUser(anyLong());
        verify(bookingRepository, times(1))
                .findById(anyLong());
        verify(bookingRepository, never())
                .save(any());
    }

    @Test
    void bookingApproveWhenBookingStatusIsNotWaiting() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;
        booking.setStatus(Status.REJECTED);
        when(validationService.checkUser(anyLong()))
                .thenReturn(user);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        try {
            bookingService.bookingApprove(userId, bookingId, approved);
        } catch (BadParameterException thrown) {
            assertThat(thrown.getMessage(), equalTo("У запроса на аренду с ID " + bookingId + " нельзя поменять статус. Текущий статус: " + booking.getStatus()));
        }

        verify(validationService, times(1))
                .checkUser(anyLong());
        verify(bookingRepository, times(1))
                .findById(anyLong());
        verify(bookingRepository, never())
                .save(any());
    }

    @Test
    void findBookingById() {
        long userId = 1L;
        long bookingId = 1L;

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        BookingDtoOut bookingDtoOut = bookingService.findBookingById(userId, bookingId);

        assertThat(bookingDtoOut.getId(), equalTo(bookingId));
        assertThat(bookingDtoOut.getStatus(), equalTo(Status.APPROVED));
        assertThat(bookingDtoOut.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(bookingDtoOut.getStart(), notNullValue());
        assertThat(bookingDtoOut.getEnd(), notNullValue());

        verify(bookingRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void findBookingByIdWhenUserNotOwnerNotBooker() {
        long userId = 3L;
        long bookingId = 1L;

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        try {
            bookingService.findBookingById(userId, bookingId);
        } catch (BookingNotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь " + userId + " не создавал бронь с ID " + bookingId +
                    " и не является владельцем вещи " + booking.getItem().getId()));
        }

        verify(bookingRepository, times(1))
                .findById(anyLong());
    }
}