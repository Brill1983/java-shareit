package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

    @Query("select b from Booking as b join fetch b.booker where b.booker.id = ?1 and b.start < ?2 and b.end > ?2 and b.status = 'APPROVED' order by b.start desc ")
    List<Booking> findAllCurrentByBookerId(Long bookerId, LocalDateTime dateTime);

    @Query("select b from Booking as b join fetch b.booker where b.booker.id = ?1 and b.end < ?2 and b.status = 'APPROVED' order by b.start desc ")
    List<Booking> findAllPastByBookerId(long userId, LocalDateTime dateTime);

//    @Query("select b from Booking as b join fetch b.booker as bo where bo.id = ?1 and b.start > ?2 and b.status = 'APPROVED' order by b.start desc ")
    @Query("select b from Booking as b join fetch b.booker as bo where bo.id = ?1 and b.start > ?2 order by b.start desc ")
    List<Booking> findAllFutureByBookerId(long userId, LocalDateTime dateTime);

    @Query("select b from Booking as b join fetch b.booker as bo where b.id = ?1 and b.status = 'WAITING' order by b.start desc ")
    List<Booking>  findAllWaitingByBookerId(long userId);

    @Query("select b from Booking as b join fetch b.booker as bo where b.id = ?1 and b.status = 'REJECTED' order by b.start desc ")
    List<Booking>  findAllRejectedByBookerId(long userId);

//    List<Booking> findAllByOwnerId(Long ownerId);

}
