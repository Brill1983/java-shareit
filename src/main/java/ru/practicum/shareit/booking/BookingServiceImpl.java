package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.exceptions.BadParameterException;
import ru.practicum.shareit.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
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
                .orElseThrow(() -> new ItemNotFoundException("Предмета с ID " + bookingDto.getItemId() + " не зарегистрировано"));
        if (!item.getAvailable()) {
            throw new BadParameterException("У выбранной для аренды вещи статус: недоступна");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new BadParameterException("В запросе аренды дата/время возврата должна быть строго позже начала аренды");
        }
        if (user.getId().equals(item.getUser().getId())) {
            throw new ItemNotFoundException("Где-то ошибка: запрос аренды отправлен от владельца вещи");
        }
        Booking booking = BookingMapper.toBooking(bookingDto, user, item);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoOut bookingApprove(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Запроса на аренду с ID " + bookingId + " не зарегистрировано"));
        if (booking.getItem().getUser().getId() != userId) {
            throw new BookingNotFoundException("У пользователя с ID " + userId + " нет запроса на аренду с ID " + bookingId);
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
                .orElseThrow(() -> new BookingNotFoundException("Запроса на аренду с ID " + bookingId + " не зарегистрировано"));
        if (booking.getBooker().getId() != userId) {
            if (booking.getItem().getUser().getId() != userId) {
                throw new BookingNotFoundException("Пользователь " + userId + " не создавал бронь с ID " + bookingId +
                        " и не является владельцем вещи " + booking.getItem().getId());
            }
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDtoOut> findUserBookings(long userId, BookingState state) {
        validationService.checkUser(userId);
        List<BookingDtoOut> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBooker_IdOrderByStartDesc(userId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookingRepository.findAllCurrentByBookerId(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookings = bookingRepository.findAllPastByBookerId(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllFutureByBookerId(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookings = bookingRepository.findAllWaitingByBookerId(userId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllRejectedByBookerId(userId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
        }
        return bookings;
    }

    @Override
    public List<BookingDtoOut> findOwnerBookings(long userId, BookingState state) {
        validationService.checkUser(userId);
        if (itemRepository.findAllByUserIdOrderById(userId).isEmpty()) {
            throw new ItemNotFoundException("Пользователь " + userId + " не является хозяином ни одной вещи");
        }
        List<BookingDtoOut> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByOwnerIdOrderByStartDesc(userId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookingRepository.findAllCurrentByOwnerId(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookings = bookingRepository.findAllPastByOwnerId(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllFutureByOwnerId(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookings = bookingRepository.findAllWaitingByOwnerId(userId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllRejectedByOwnerId(userId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
        }
        return bookings;
    }
}
