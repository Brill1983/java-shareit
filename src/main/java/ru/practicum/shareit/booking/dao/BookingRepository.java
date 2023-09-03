package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

//    @Query("select b from Booking as b where b.booker.id = ?1 order by b.start desc ")
//    List<Booking> findAllByBookerId(Long bookerId);

    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

//    @Query("select b from Booking as b where b.booker.id = ?1 and b.start < current_timestamp and b.end > current_timestamp and b.status = 'APPROVED' order by b.start desc ") //TODO переписать на нативный язык
//    List<Booking> findAllCurrentByBookerId(Long bookerId);

    List<Booking> findByBooker_IdAndStartBeforeAndEndAfterAndStatus_ApprovedOrderByStartDesc(Long bookerId, LocalDateTime dateTime);

//    @Query("select b from Booking as b where b.booker.id = ?1 and b.end < local_datetime and b.status = 'APPROVED' order by b.start desc ")
//    List<Booking> findAllPastByBookerId(long userId);

    List<Booking> findByBooker_IdAndStatus_ApprovedAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime dateTime);

    @Query("select b from Booking as b where b.booker.id = ?1 and b.start > current_timestamp and b.status = 'APPROVED' order by b.start desc ")
    List<Booking> findAllFutureByBookerId(long userId);

    @Query("select b from Booking as b where b.booker.id = ?1 and b.status = 'WAITING' order by b.start desc ")
    List<Booking>  findAllWaitingByBookerId(long userId);

    @Query("select b from Booking as b where b.booker.id = ?1 and b.status = 'REJECTED' order by b.start desc ")
    List<Booking>  findAllRejectedByBookerId(long userId);

//    List<Booking> findAllByOwnerId(Long ownerId);

}
