package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.booking.Constants.HEADER;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    private BookingServiceImpl bookingService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto;
    private BookingDtoIn bookingDtoIn;
    private BookingDtoOut bookingDtoOut;
    private UserDto userDto;

    @BeforeEach
    public void itemCreate() {
        itemDto = new ItemDto(1L, "Вещь 1", "Описание вещи 1", true, null);
        userDto = new UserDto(1L, "Иван Иванович", "ii@mail.ru");
        bookingDtoOut = new BookingDtoOut(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(5),
                itemDto, userDto, Status.APPROVED);
        bookingDtoIn = new BookingDtoIn(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(5),
                itemDto.getId(), Status.APPROVED);
    }

    @Test
    void saveBooking() throws Exception {
        when(bookingService.saveBooking(anyLong(), any()))
                .thenReturn(bookingDtoOut);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));

        verify(bookingService, times(1))
                .saveBooking(anyLong(), any());
    }

    @Test
    void bookingApprove() throws Exception {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;

        when(bookingService.bookingApprove(userId, bookingId, approved))
                .thenReturn(bookingDtoOut);

        mvc.perform(patch("/bookings/1")
                        .header(HEADER, 1)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));

        verify(bookingService, times(1))
                .bookingApprove(userId, bookingId, approved);
    }

    @Test
    void findBookingById() throws Exception {
        long userId = 1L;
        long bookingId = 1L;

        when(bookingService.findBookingById(userId, bookingId))
                .thenReturn(bookingDtoOut);

        mvc.perform(get("/bookings/1")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));

        verify(bookingService, times(1))
                .findBookingById(userId, bookingId);
    }


    @Test
    void findUserBookings() throws Exception {
        long userId = 1L;
        String state = "FUTURE";
        BookingState enumState = BookingState.FUTURE;
        int from = 0;
        int size = 5;
        List<BookingDtoOut> bookingList = List.of(bookingDtoOut);

        when(bookingService.findUserBookings(userId, enumState, from, size))
                .thenReturn(bookingList);

        mvc.perform(get("/bookings")
                        .header(HEADER, 1)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingList)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDtoOut.getStatus().toString())))
                .andExpect(jsonPath("$[0].start", notNullValue()))
                .andExpect(jsonPath("$[0].end", notNullValue()));

        verify(bookingService, times(1))
                .findUserBookings(userId, enumState, from, size);
    }

    @Test
    void findOwnerBookings() throws Exception {
        long userId = 1L;
        String state = "FUTURE";
        BookingState enumState = BookingState.FUTURE;
        int from = 0;
        int size = 5;
        List<BookingDtoOut> bookingList = List.of(bookingDtoOut);

        when(bookingService.findOwnerBookings(userId, enumState, from, size))
                .thenReturn(bookingList);

        mvc.perform(get("/bookings/owner")
                        .header(HEADER, 1)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(bookingList)))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDtoOut.getStatus().toString())))
                .andExpect(jsonPath("$[0].start", notNullValue()))
                .andExpect(jsonPath("$[0].end", notNullValue()));

        verify(bookingService, times(1))
                .findOwnerBookings(userId, enumState, from, size);
    }
}