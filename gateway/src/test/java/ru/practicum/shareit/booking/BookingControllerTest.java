package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.service.Constants.HEADER;


@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private BookingDto bookingDto;
    private ResponseEntity<Object> response;

    @BeforeEach
    public void itemCreate() {
        bookingDto = new BookingDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(5),
                1L, Status.APPROVED);
        response = new ResponseEntity<>(bookingDto, HttpStatus.OK);
    }

    @Test
    void saveBooking() throws Exception {
        when(bookingClient.saveBooking(anyLong(), any()))
                .thenReturn(response);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.itemId", notNullValue()))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));

        verify(bookingClient, times(1))
                .saveBooking(anyLong(), any());
    }

    @Test
    void saveBookingWithEndBeforeStart() throws Exception {
        bookingDto.setStart(bookingDto.getEnd().plusMinutes(2));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never())
                .saveBooking(anyLong(), any());
    }

    @Test
    void saveBookingWithStartIsNull() throws Exception {
        bookingDto.setStart(null);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never())
                .saveBooking(anyLong(), any());
    }

    @Test
    void saveBookingWithStartIsPast() throws Exception {
        bookingDto.setStart(LocalDateTime.now().minusDays(1));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never())
                .saveBooking(anyLong(), any());
    }

    @Test
    void saveBookingWithEndIsNull() throws Exception {
        bookingDto.setEnd(null);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never())
                .saveBooking(anyLong(), any());
    }

    @Test
    void saveBookingWithNullItemId() throws Exception {
        bookingDto.setItemId(null);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never())
                .saveBooking(anyLong(), any());
    }

    @Test
    void bookingApprove() throws Exception {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;

        when(bookingClient.bookingApprove(userId, bookingId, approved))
                .thenReturn(response);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(HEADER, userId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));

        verify(bookingClient, times(1))
                .bookingApprove(userId, bookingId, approved);
    }

    @Test
    void findBookingById() throws Exception {
        long userId = 1L;
        long bookingId = 1L;

        when(bookingClient.findBookingById(userId, bookingId))
                .thenReturn(response);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));

        verify(bookingClient, times(1))
                .findBookingById(userId, bookingId);
    }


    @Test
    void findUserBookings() throws Exception {
        long userId = 1L;
        String state = "FUTURE";
        BookingState enumState = BookingState.FUTURE;
        int from = 0;
        int size = 5;
        List<BookingDto> bookingList = List.of(bookingDto);
        ResponseEntity<Object> responseWithList = new ResponseEntity<>(bookingList, HttpStatus.OK);

        when(bookingClient.findUserBookings(userId, enumState, from, size))
                .thenReturn(responseWithList);

        mvc.perform(get("/bookings")
                        .header(HEADER, userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingList)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].start", notNullValue()))
                .andExpect(jsonPath("$[0].end", notNullValue()));

        verify(bookingClient, times(1))
                .findUserBookings(userId, enumState, from, size);
    }

    @Test
    void findUserBookingsWithWrongFromParam() throws Exception {
        long userId = 1L;
        String state = "FUTURE";
        BookingState enumState = BookingState.FUTURE;
        int from = -1;
        int size = 5;

        mvc.perform(get("/bookings")
                        .header(HEADER, 1)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never())
                .findUserBookings(userId, enumState, from, size);
    }

    @Test
    void findUserBookingsWithNullSizeParam() throws Exception {
        long userId = 1L;
        String state = "FUTURE";
        BookingState enumState = BookingState.FUTURE;
        int from = 0;
        int size = 0;

        mvc.perform(get("/bookings")
                        .header(HEADER, 1)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never())
                .findUserBookings(userId, enumState, from, size);
    }

    @Test
    void findUserBookingsWithNegativeSizeParam() throws Exception {
        long userId = 1L;
        String state = "FUTURE";
        BookingState enumState = BookingState.FUTURE;
        int from = 0;
        int size = -5;

        mvc.perform(get("/bookings")
                        .header(HEADER, userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never())
                .findUserBookings(userId, enumState, from, size);
    }

    @Test
    void findOwnerBookings() throws Exception {
        long userId = 1L;
        String state = "FUTURE";
        BookingState enumState = BookingState.FUTURE;
        int from = 0;
        int size = 5;
        List<BookingDto> bookingList = List.of(bookingDto);
        ResponseEntity<Object> responseWithList = new ResponseEntity<>(bookingList, HttpStatus.OK);

        when(bookingClient.findOwnerBookings(userId, enumState, from, size))
                .thenReturn(responseWithList);

        mvc.perform(get("/bookings/owner")
                        .header(HEADER, userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(bookingList)))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].start", notNullValue()))
                .andExpect(jsonPath("$[0].end", notNullValue()));

        verify(bookingClient, times(1))
                .findOwnerBookings(userId, enumState, from, size);
    }

    @Test
    void findOwnersBookingsWithWrongFromParam() throws Exception {
        long userId = 1L;
        String state = "FUTURE";
        BookingState enumState = BookingState.FUTURE;
        int from = -1;
        int size = 5;

        mvc.perform(get("/bookings/owner")
                        .header(HEADER, 1)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never())
                .findOwnerBookings(userId, enumState, from, size);
    }

    @Test
    void findOwnersBookingsWithNullSizeParam() throws Exception {
        long userId = 1L;
        String state = "FUTURE";
        BookingState enumState = BookingState.FUTURE;
        int from = 0;
        int size = 0;

        mvc.perform(get("/bookings/owner")
                        .header(HEADER, 1)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never())
                .findOwnerBookings(userId, enumState, from, size);
    }

    @Test
    void findOwnersBookingsWithNegativeSizeParam() throws Exception {
        long userId = 1L;
        String state = "FUTURE";
        BookingState enumState = BookingState.FUTURE;
        int from = 0;
        int size = -5;

        mvc.perform(get("/bookings/owner")
                        .header(HEADER, 1)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never())
                .findOwnerBookings(userId, enumState, from, size);
    }
}