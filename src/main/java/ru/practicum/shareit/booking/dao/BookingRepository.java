package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

    @Query("select b " +
            "from Booking as b " +
            "join b.booker as bo  " +
            "where bo.id = ?1 and b.start < ?2 and b.end > ?2 " +
            "order by b.start desc ")
    List<Booking> findAllCurrentByBookerId(Long bookerId, LocalDateTime dateTime);

    @Query("select b " +
            "from Booking as b " +
            "join b.booker as bo  " +
            "where bo.id = ?1 and b.end < ?2 and b.status = 'APPROVED' " +
            "order by b.start desc ")
    List<Booking> findAllPastByBookerId(long userId, LocalDateTime dateTime);

    @Query("select b " +
            "from Booking as b " +
            "join b.booker as bo " +
            "where bo.id = ?1 and b.start > ?2 " +
            "order by b.start desc ")
    List<Booking> findAllFutureByBookerId(long userId, LocalDateTime dateTime);

    @Query("select b " +
            "from Booking as b " +
            "join b.booker as bo " +
            "where bo.id = ?1 and b.status = 'WAITING' " +
            "order by b.start desc ")
    List<Booking> findAllWaitingByBookerId(long userId);

    @Query("select b " +
            "from Booking as b " +
            "join b.booker as bo " +
            "where bo.id = ?1 and b.status = 'REJECTED' " +
            "order by b.start desc ")
    List<Booking> findAllRejectedByBookerId(long userId);


    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "join i.user as u " +
            "where u.id = ?1 " +
            "order by b.start desc")
    List<Booking> findByOwnerIdOrderByStartDesc(Long ownerId);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "join i.user as u " +
            "where u.id = ?1 and b.start < ?2 and b.end > ?2 " +
            "order by b.start desc ")
    List<Booking> findAllCurrentByOwnerId(Long ownerId, LocalDateTime dateTime);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "join i.user as u " +
            "where u.id = ?1 and b.end < ?2 and b.status = 'APPROVED' " +
            "order by b.start desc ")
    List<Booking> findAllPastByOwnerId(Long ownerId, LocalDateTime dateTime);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "join i.user as u " +
            "where u.id = ?1 and b.start > ?2 " +
            "order by b.start desc ")
    List<Booking> findAllFutureByOwnerId(Long ownerId, LocalDateTime dateTime);


    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "join i.user as u " +
            "where u.id = ?1 and b.status = 'WAITING' " +
            "order by b.start desc ")
    List<Booking> findAllWaitingByOwnerId(Long ownerId);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "join i.user as u " +
            "where u.id = ?1 and b.status = 'REJECTED' " +
            "order by b.start desc ")
    List<Booking> findAllRejectedByOwnerId(Long ownerId);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.id = ?1 and b.start < ?2 and b.status = 'APPROVED'" +
            "order by b.start desc")
    List<Booking> findLastBookingByItemId(Long itemId, LocalDateTime dateTime);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.id = ?1 and b.start > ?2 and b.status = 'APPROVED'" +
            "order by b.start")
    List<Booking> findNextBookingByItemId(Long itemId, LocalDateTime dateTime);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i join i.user as u " +
            "where u.id = ?1 and b.start < ?2 " + //b.end < ?2 and
            "order by b.start desc")
    List<Booking> findLastBookingsByUserId(Long userId, LocalDateTime dateTime);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i join i.user as u " +
            "where u.id = ?1 and b.start > ?2 " +
            "order by b.start")
    List<Booking> findNextBookingsByUserId(Long userId, LocalDateTime dateTime);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "join b.booker as bo " +
            "where bo.id = ?1 and i.id = ?2 and b.status = 'APPROVED' and b.start < ?3 and b.end < ?3")
    List<Booking> findAllByBookerIdAndItemId(Long userId, Long itemId, LocalDateTime dateTime);

}
