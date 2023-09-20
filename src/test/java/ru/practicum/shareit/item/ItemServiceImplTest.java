package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.exceptions.BadParameterException;
import ru.practicum.shareit.exceptions.ElementNotFoundException;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    private ValidationService validationService;
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private RequestRepository requestRepository;
    private ItemServiceImpl service;

    private ItemDto itemDto;
    private Comment comment;
    private CommentDto commentDto;
    private Booking booking;
    private User user;
    private Item item;

    @BeforeEach
    void beforeEach() {
        itemDto = new ItemDto(1L, "Вещь 1", "Описание вещи 1", true, null);
        commentDto = new CommentDto(1L, "Коммент 1", itemDto, "Иван Иванович", LocalDateTime.now());
        user = new User(1L, "Иван Иванович", "ii@mail.ru");
        item = new Item(1L, "Вещь 1", "Описание вещи 1", true, user, null);
        booking = new Booking(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1), item, user, Status.APPROVED);
        comment = new Comment(1L, "Коммент 1", item, user, LocalDateTime.now());

        validationService = mock(ValidationService.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        requestRepository = mock(RequestRepository.class);
        service = new ItemServiceImpl(itemRepository, bookingRepository, validationService, commentRepository, requestRepository);
    }

    @Test
    void createItem() {
        long userId = 1L;
        when(validationService.checkUser(anyLong()))
                .thenReturn(user);
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDto itemFromMethod = service.createItem(userId, itemDto);

        assertThat(itemFromMethod.getId(), equalTo(item.getId()));
        assertThat(itemFromMethod.getName(), equalTo(item.getName()));
        assertThat(itemFromMethod.getDescription(), equalTo(item.getDescription()));
        assertThat(itemFromMethod.getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemFromMethod.getRequestId(), nullValue());

        verify(validationService, times(1))
                .checkUser(anyLong());
        verify(requestRepository, never())
                .findById(anyLong());
        verify(itemRepository, times(1))
                .save(any());
    }

    @Test
    void createItemWithWrongUserId() {
        long userId = 1L;
        when(validationService.checkUser(anyLong()))
                .thenThrow(new ElementNotFoundException("Пользователь с таким ID не зарегистрировано"));
        try {
            service.createItem(userId, itemDto);
        } catch (ElementNotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с таким ID не зарегистрировано"));
        }

        verify(validationService, times(1))
                .checkUser(anyLong());
        verify(requestRepository, never())
                .findById(anyLong());
        verify(itemRepository, never())
                .save(any());
    }

    @Test
    void createItemWithWrongRequestId() {
        long requestid = 2L;
        itemDto.setRequestId(requestid);
        when(validationService.checkUser(anyLong()))
                .thenReturn(user);
        when(requestRepository.findById(itemDto.getRequestId()))
                .thenThrow(new ElementNotFoundException("Запроса с ID " + itemDto.getRequestId() + " нет в базе"));
        try {
            service.createItem(1L, itemDto);
        } catch (ElementNotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Запроса с ID " + itemDto.getRequestId() + " нет в базе"));
        }

        verify(validationService, times(1))
                .checkUser(anyLong());
        verify(requestRepository, times(1))
                .findById(anyLong());
        verify(itemRepository, never())
                .save(any());
    }

    @Test
    void updateItem() {
        long userId = 1L;
        long itemId = 1L;
        when(validationService.checkUser(anyLong()))
                .thenReturn(user);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDto itemFromMethod = service.updateItem(userId, itemDto, itemId);

        assertThat(itemFromMethod.getId(), equalTo(itemId));
        assertThat(itemFromMethod.getName(), equalTo(itemDto.getName()));
        assertThat(itemFromMethod.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(itemFromMethod.getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemFromMethod.getRequestId(), nullValue());

        verify(validationService, times(1))
                .checkUser(anyLong());
        verify(itemRepository, times(1))
                .findById(anyLong());
        verify(itemRepository, times(1))
                .save(any());
        verify(requestRepository, never())
                .findById(anyLong());
        verify(itemRepository, times(1))
                .save(any());
    }

    @Test
    void updateItemNotByOwner() {
        User user2 = new User(2L, "Петр Петрович", "pp@mail.ru");
        item.setUser(user2);
        long userId = 1L;
        long itemId = 1L;

        when(validationService.checkUser(anyLong()))
                .thenReturn(user);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        try {
            service.updateItem(userId, itemDto, itemId);
        } catch (ElementNotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с ID " + userId + " не является владельцем вещи c ID " + itemId + ". Изменение запрещено"));
        }
        verify(validationService, times(1))
                .checkUser(anyLong());
        verify(itemRepository, times(1))
                .findById(anyLong());
        verify(itemRepository, never())
                .save(any());
        verify(requestRepository, never())
                .findById(anyLong());
    }

    @Test
    void saveComment() {
        long userId = 1L;
        long itemId = 1L;
        when(validationService.checkUser(anyLong()))
                .thenReturn(user);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository
                .findAllByItem_IdAndBooker_IdAndStatusAndStartBeforeAndEndBefore(anyLong(), anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentDto commentFromMethod = service.saveComment(userId, itemId, commentDto);

        assertThat(commentFromMethod.getId(), equalTo(commentDto.getId()));
        assertThat(commentFromMethod.getText(), equalTo(commentDto.getText()));
        assertThat(commentFromMethod.getAuthorName(), equalTo(commentDto.getAuthorName()));
        assertThat(commentFromMethod.getItem().getId(), equalTo(commentDto.getItem().getId()));
        assertThat(commentFromMethod.getCreated(), equalTo(commentDto.getCreated()));

        verify(validationService, times(1))
                .checkUser(anyLong());
        verify(itemRepository, times(1))
                .findById(anyLong());
        verify(bookingRepository, times(1))
                .findAllByItem_IdAndBooker_IdAndStatusAndStartBeforeAndEndBefore(anyLong(), anyLong(), any(), any(), any());
        verify(commentRepository, times(1))
                .save(any());
    }

    @Test
    void commentSaveFailUserHasNoPastBookings() {
        long userId = 1L;
        long itemId = 1L;
        when(validationService.checkUser(anyLong()))
                .thenReturn(user);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository
                .findAllByItem_IdAndBooker_IdAndStatusAndStartBeforeAndEndBefore(anyLong(), anyLong(), any(), any(), any()))
                .thenReturn(Collections.emptyList());
        try {
            service.saveComment(userId, itemId, commentDto);
        } catch (BadParameterException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь " + userId + " не арендовал вещь " + itemId + ". Не имеет права писать отзыв"));
        }

        verify(validationService, times(1))
                .checkUser(anyLong());
        verify(itemRepository, times(1))
                .findById(anyLong());
        verify(bookingRepository, times(1))
                .findAllByItem_IdAndBooker_IdAndStatusAndStartBeforeAndEndBefore(anyLong(), anyLong(), any(), any(), any());
        verify(commentRepository, never())
                .save(any());
    }
}