package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BadParameterException;
import ru.practicum.shareit.exceptions.ElementNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.service.ValidationService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final ValidationService validationService;

    @Override
    @Transactional
    public BookingDtoOut saveBooking(long userId, BookingDtoIn bookingDto) {
        User user = validationService.checkUser(userId);
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ElementNotFoundException("Предмета с ID " + bookingDto.getItemId()
                        + " не зарегистрировано"));
        if (!item.getAvailable()) {
            throw new BadParameterException("У выбранной для аренды вещи статус: недоступна");
        }
        if (user.getId().equals(item.getUser().getId())) {
            throw new ElementNotFoundException("Где-то ошибка: запрос аренды отправлен от владельца вещи");
        }
        Booking booking = BookingMapper.toBooking(bookingDto, user, item);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoOut bookingApprove(long userId, long bookingId, boolean approved) {
        validationService.checkUser(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ElementNotFoundException("Запроса на аренду с ID " + bookingId
                        + " не зарегистрировано"));
        if (booking.getItem().getUser().getId() != userId) {
            throw new ElementNotFoundException("Пользователь ID " + userId + " не является владельцем вещи с ID "
                    + booking.getItem().getId() + " и не может менять одобрить/отклонить запрос на аренду этой вещи");
        }
        if (booking.getStatus() == Status.WAITING) {
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
        } else {
            throw new BadParameterException("У запроса на аренду с ID " + bookingId +
                    " нельзя поменять статус. Текущий статус: " + booking.getStatus());
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoOut findBookingById(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ElementNotFoundException("Запроса на аренду с ID " + bookingId
                        + " не зарегистрировано"));
        if (booking.getBooker().getId() != userId) {
            if (booking.getItem().getUser().getId() != userId) {
                throw new ElementNotFoundException("Пользователь " + userId + " не создавал бронь с ID " + bookingId +
                        " и не является владельцем вещи " + booking.getItem().getId());
            }
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDtoOut> findUserBookings(long userId, BookingState state, int from, int size) {
        validationService.checkUser(userId);

        Pageable page = PageRequest.of(from / size, size);

        List<BookingDtoOut> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findBookingsByBooker_IdOrderByStartDesc(userId, page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStart(userId,
                                LocalDateTime.now(), LocalDateTime.now(), page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookings = bookingRepository.findAllByBooker_IdAndEndBeforeAndStatusOrderByStartDesc(userId,
                                LocalDateTime.now(), Status.APPROVED, page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(userId,
                                LocalDateTime.now(), page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, Status.WAITING, page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, Status.REJECTED, page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
        }
        return bookings;
    }

    @Override
    public List<BookingDtoOut> findOwnerBookings(long userId, BookingState state, int from, int size) {
        validationService.checkUser(userId);

        Pageable page = PageRequest.of(from / size, size);

        List<BookingDtoOut> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItem_User_IdOrderByStartDesc(userId, page).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItem_User_IdAndStartBeforeAndEndAfterOrderByStart(userId,
                                LocalDateTime.now(), LocalDateTime.now(), page).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookings = bookingRepository.findAllByItem_User_IdAndEndBeforeAndStatusOrderByStartDesc(userId,
                                LocalDateTime.now(), Status.APPROVED, page).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItem_User_IdAndStartAfterOrderByStartDesc(userId,
                                LocalDateTime.now(), page).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItem_User_IdAndStatusOrderByStartDesc(userId,
                                Status.WAITING, page).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItem_User_IdAndStatusOrderByStartDesc(userId,
                                Status.REJECTED, page).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
        }
        return bookings;
    }
}
