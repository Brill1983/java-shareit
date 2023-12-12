package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exceptions.BadParameterException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.service.Constants.HEADER;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> saveBooking(
            @RequestHeader(HEADER) long userId,
            @RequestBody @Valid BookingDto bookingDto) {
        log.info("В метод saveBooking передан userId {}, bookingDto.itemId {}, bookingDto.start {}, bookingDto.end {}",
                userId, bookingDto.getItemId(), bookingDto.getStart(), bookingDto.getEnd());
        return bookingClient.saveBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> bookingApprove(@RequestHeader(HEADER) long userId,
                                                 @PathVariable long bookingId,
                                                 @RequestParam boolean approved) {
        log.info("В метод bookingApprove передан userId {}, bookingId {}, статус подтверждения {}",
                userId, bookingId, approved);
        return bookingClient.bookingApprove(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findBookingById(@RequestHeader(HEADER) long userId,
                                                  @PathVariable Long bookingId) {
        log.info("В метод findBookingById передан userId {}, bookingId {}", userId, bookingId);
        return bookingClient.findBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findUserBookings(@RequestHeader(HEADER) long userId,
                                                   @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BadParameterException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.findUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findOwnerBookings(@RequestHeader(HEADER) long userId,
                                                    @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                    @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                                    @RequestParam(name = "size", defaultValue = "20") @Positive int size) {
        log.info("В метод findOwnerBookings передан userId {}, статус бронирования для поиска {}, " +
                "индекс первого элемента {}, количество элементов на странице {}", userId, stateParam, from, size);
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BadParameterException("Unknown state: " + stateParam));
        return bookingClient.findOwnerBookings(userId, state, from, size);
    }
}
