package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    public static final String HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOut saveBooking(
            @RequestHeader(HEADER) long userId,
            @RequestBody @Valid BookingDtoIn bookingDto) {
        return bookingService.saveBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut bookingApprove(@RequestHeader(HEADER) long userId,
                                        @PathVariable long bookingId,
                                        @RequestParam boolean approved) {
        return bookingService.bookingApprove(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut findBookingById(@RequestHeader(HEADER) long userId, @PathVariable long bookingId) {
        return bookingService.findBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoOut> findUserBookings(@RequestHeader(HEADER) long userId,
                                                @RequestParam(defaultValue = "all") String state) {
        return bookingService.findUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> findOwnerBookings(@RequestHeader(HEADER) long userId,
                                                 @RequestParam(defaultValue = "all") String state) {
        return bookingService.findOwnerBookings(userId, state);
    }
}
