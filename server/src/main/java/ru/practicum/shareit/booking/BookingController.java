package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.exceptions.BadParameterException;

import java.util.List;

import static ru.practicum.shareit.booking.Constants.HEADER;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOut saveBooking(
            @RequestHeader(HEADER) long userId,
            @RequestBody BookingDtoIn bookingDto) {
        log.info("В метод saveBooking передан userId {}, bookingDto.itemId {}, bookingDto.start {}, bookingDto.end {}",
                userId, bookingDto.getItemId(), bookingDto.getStart(), bookingDto.getEnd());
        return bookingService.saveBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut bookingApprove(@RequestHeader(HEADER) long userId,
                                        @PathVariable long bookingId,
                                        @RequestParam boolean approved) {
        log.info("В метод bookingApprove передан userId {}, bookingId {}, статус подтверждения {}",
                userId, bookingId, approved);
        return bookingService.bookingApprove(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut findBookingById(@RequestHeader(HEADER) long userId, @PathVariable long bookingId) {
        log.info("В метод findBookingById передан userId {}, bookingId {}", userId, bookingId);
        return bookingService.findBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoOut> findUserBookings(@RequestHeader(HEADER) long userId,
                                                @RequestParam(defaultValue = "all") String state,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "20") int size) {
        log.info("В метод findUserBookings передан userId {}, статус бронирования для поиска {}, " +
                "индекс первого элемента {}, количество элементов на странице {}", userId, state, from, size);
        BookingState enumState = BookingState.from(state)
                .orElseThrow(() -> new BadParameterException("Unknown state: " + state));
        return bookingService.findUserBookings(userId, enumState, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> findOwnerBookings(@RequestHeader(HEADER) long userId,
                                                 @RequestParam(defaultValue = "all") String state,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "20") int size) {
        log.info("В метод findOwnerBookings передан userId {}, статус бронирования для поиска {}, " +
                "индекс первого элемента {}, количество элементов на странице {}", userId, state, from, size);
        BookingState enumState = BookingState.from(state)
                .orElseThrow(() -> new BadParameterException("Unknown state: " + state));
        return bookingService.findOwnerBookings(userId, enumState, from, size);
    }
}
