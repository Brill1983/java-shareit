package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoDated;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ItemServiceImplTestWithContext {

    private final EntityManager em;

    private final ItemService itemService;

    private final UserService userService;

    private final BookingService bookingService;

    private ItemDto itemDto;
    private BookingDtoIn bookingLastDtoIn;
    private BookingDtoIn bookingNextDtoIn;
    private UserDto userDto;
    private User user;

    @BeforeEach
    void beforeEach() {
        itemDto = new ItemDto(1L, "Вещь 1", "Описание вещи 1", true, null);
        bookingLastDtoIn = new BookingDtoIn(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(5), 1L, Status.APPROVED);
        bookingNextDtoIn = new BookingDtoIn(2L, LocalDateTime.now().plusHours(12), LocalDateTime.now().plusDays(1), 1L, Status.APPROVED);
        userDto = new UserDto(1L, "Иван Иванович", "ii@mail.ru");
        user = new User(1L, "Иван Иванович", "ii@mail.ru");

        userService.createUser(userDto);
    }


    @Test
    void search() {
        itemService.createItem(userDto.getId(), itemDto);
        int from = 0;
        int size = 5;
        String text = "ещ";
        List<ItemDto> itemsList = itemService.search(text, from, size);

        TypedQuery<Item> query = em.createQuery("select it from Item as it where it.available = true and " +
                "(upper(it.name) like upper(concat('%', :text, '%')) or upper(it.description) " +
                "like upper(concat('%', :text, '%'))) ", Item.class);
        List<Item> itemsFromDb = query.setParameter("text", text)
                .getResultList();

        assertThat(itemsList.size(), equalTo(itemsFromDb.size()));
        assertThat(itemsList.get(0).getId(), equalTo(itemsFromDb.get(0).getId()));
        assertThat(itemsList.get(0).getName(), equalTo(itemsFromDb.get(0).getName()));
        assertThat(itemsList.get(0).getDescription(), equalTo(itemsFromDb.get(0).getDescription()));
        assertThat(itemsList.get(0).getAvailable(), equalTo(itemsFromDb.get(0).getAvailable()));
    }

    @Test
    void searchForNoItems() {
        itemService.createItem(userDto.getId(), itemDto);
        int from = 0;
        int size = 5;
        String text = "вещьстакойстрокойненайти";
        List<ItemDto> itemsList = itemService.search(text, from, size);

        assertThat(itemsList, empty());
    }

    @Test
    void getItemByIdWhenUserIsOwnerOfItem() {
        long userId = 1L;
        long itemId = 1L;
        UserDto user2 = new UserDto(2L, "Петр Петрович", "pp@mail.ru");
        userService.createUser(user2);
        itemService.createItem(userDto.getId(), itemDto);
        bookingService.saveBooking(2l, bookingLastDtoIn);
        bookingService.saveBooking(2l, bookingNextDtoIn);

        ItemDtoDated item = itemService.getItemById(userId, itemId);
        assertThat(item.getId(), equalTo(itemId));
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(item.getComments(), empty());

        assertThat(item.getLastBooking().getId(), equalTo(bookingLastDtoIn.getId()));
        assertThat(item.getLastBooking().getEnd(), notNullValue());
        assertThat(item.getLastBooking().getStart(), notNullValue());
        assertThat(item.getLastBooking().getStatus(), equalTo(bookingLastDtoIn.getStatus()));
        assertThat(item.getLastBooking().getBookerId(), equalTo(user2.getId()));

        assertThat(item.getNextBooking().getId(), equalTo(bookingNextDtoIn.getId()));
        assertThat(item.getNextBooking().getEnd(), notNullValue());
        assertThat(item.getNextBooking().getStart(), notNullValue());
        assertThat(item.getNextBooking().getStatus(), equalTo(bookingNextDtoIn.getStatus()));
        assertThat(item.getNextBooking().getBookerId(), equalTo(user2.getId()));

    }

    @Test
    void getItemByIdWhenUserIsNotOwnerOfItem() {
        long userId = 2L;
        long itemId = 1L;
        UserDto user2 = new UserDto(2L, "Петр Петрович", "pp@mail.ru");
        userService.createUser(user2);
        itemService.createItem(userDto.getId(), itemDto);
        bookingService.saveBooking(user2.getId(), bookingLastDtoIn);
        bookingService.saveBooking(user2.getId(), bookingNextDtoIn);

        ItemDtoDated item = itemService.getItemById(userId, itemId);
        assertThat(item.getId(), equalTo(itemId));
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(item.getComments(), empty());

        assertThat(item.getLastBooking(), nullValue());

        assertThat(item.getNextBooking(), nullValue());
    }

    @Test
    void getUserItems() {
        long userId = 1L;
        int from = 0;
        int size = 5;
        UserDto user2 = new UserDto(2L, "Петр Петрович", "pp@mail.ru");
        userService.createUser(user2);

        itemService.createItem(userDto.getId(), itemDto);
        bookingService.saveBooking(user2.getId(), bookingLastDtoIn);
        bookingService.saveBooking(user2.getId(), bookingNextDtoIn);

        List<ItemDtoDated> itemsList = itemService.getUserItems(userId, from, size);

        assertThat(itemsList.size(), equalTo(1));

        assertThat(itemsList.get(0).getId(), equalTo(itemDto.getId()));
        assertThat(itemsList.get(0).getName(), equalTo(itemDto.getName()));
        assertThat(itemsList.get(0).getDescription(), equalTo(itemDto.getDescription()));
        assertThat(itemsList.get(0).getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(itemsList.get(0).getComments(), empty());

        assertThat(itemsList.get(0).getLastBooking().getId(), equalTo(bookingLastDtoIn.getId()));
        assertThat(itemsList.get(0).getLastBooking().getEnd(), notNullValue());
        assertThat(itemsList.get(0).getLastBooking().getStart(), notNullValue());
        assertThat(itemsList.get(0).getLastBooking().getStatus(), equalTo(bookingLastDtoIn.getStatus()));
        assertThat(itemsList.get(0).getLastBooking().getBookerId(), equalTo(user2.getId()));

        assertThat(itemsList.get(0).getNextBooking().getId(), equalTo(bookingNextDtoIn.getId()));
        assertThat(itemsList.get(0).getNextBooking().getEnd(), notNullValue());
        assertThat(itemsList.get(0).getNextBooking().getStart(), notNullValue());
        assertThat(itemsList.get(0).getNextBooking().getStatus(), equalTo(bookingNextDtoIn.getStatus()));
        assertThat(itemsList.get(0).getNextBooking().getBookerId(), equalTo(user2.getId()));
    }
}
