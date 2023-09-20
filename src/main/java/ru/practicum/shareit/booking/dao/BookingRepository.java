package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // ПОИСК ПО БУКЕРУ
    List<Booking> findBookingsByBooker_IdOrderByStartDesc(Long bookerId, Pageable page);

    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStart(Long bookerId, LocalDateTime start, LocalDateTime end, Pageable page);

    List<Booking> findAllByBooker_IdAndEndBeforeAndStatusOrderByStartDesc(Long userId, LocalDateTime dateTime, Status status, Pageable page);


    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(long userId, LocalDateTime dateTime, Pageable page);

    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(long userId, Status status, Pageable page);

    //ПОИСК ПО ХОЗЯИНУ ВЕЩИ
    List<Booking> findAllByItem_User_IdOrderByStartDesc(Long ownerId, Pageable page);

    List<Booking> findAllByItem_User_IdAndStartBeforeAndEndAfterOrderByStart(Long ownerId, LocalDateTime start, LocalDateTime end, Pageable page);

    List<Booking> findAllByItem_User_IdAndEndBeforeAndStatusOrderByStartDesc(Long ownerId, LocalDateTime end, Status status, Pageable page);

    List<Booking> findAllByItem_User_IdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime start, Pageable page);

    List<Booking> findAllByItem_User_IdAndStatusOrderByStartDesc(Long ownerId, Status status, Pageable page);

    // Поиск last и next booking для item
    Optional<Booking> findFirstByItem_IdAndStartBeforeAndStatusOrderByStartDesc(Long itemId, LocalDateTime start, Status status);

    Optional<Booking> findFirstByItem_IdAndStartAfterAndStatusOrderByStart(Long itemId, LocalDateTime start, Status status);

    List<Booking> findAllByItem_User_IdAndItem_IdInAndStartBeforeOrderByStartDesc(Long userId, List<Long> itemIds, LocalDateTime start);

    List<Booking> findAllByItem_User_IdAndItem_IdInAndStartAfterOrderByStart(Long itemId, List<Long> itemIds, LocalDateTime start);

    List<Booking> findAllByItem_IdAndBooker_IdAndStatusAndStartBeforeAndEndBefore(Long itemId, Long bookerId, Status status, LocalDateTime start, LocalDateTime end);


}
