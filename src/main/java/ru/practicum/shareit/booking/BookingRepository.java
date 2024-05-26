package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByUserId(Long userId, Pageable page);

    Page<Booking> findByUserIdOrderByStartDesc(Long userId, Pageable page);

    @Query("select b from Booking as b join b.user as u join b.item as i where u.id = ?1 and i.id = ?2")
    List<Booking> findUsersBookingForAnItemOrderByStartDesc(Long userId, Long itemId);

    @Query("select b from Booking as b join b.item as i where i.id = ?1 order by b.start desc")
    List<Booking> findByItemIdOrderByStartDesc(Long itemId);

    @Query("select b from Booking as b join b.item as i where i.owner = ?1 ")
    Page<Booking> findAllOwnerBookings(Long ownerId, Pageable page);

    @Query("select b from Booking as b join b.item as i where i.owner = ?1 order by b.start desc")
    Page<Booking> findAllOwnerBookingsOrderByStartDesc(Long ownerId, Pageable page);

    @Query("select b from Booking as b join b.item as i where i.owner = ?1 and b.status = ?2")
    Page<Booking> findAllOwnerBookingsAndStatus(Long ownerId, Status status, Pageable page);

    Page<Booking> findByUserIdAndStatus(Long userId, Status status, Pageable page);

    @Query("select b from Booking as b join b.item as i where i.owner = ?1 and b.end < ?2")
    Page<Booking> findByOwnerIdAndEndBefore(Long ownerId, LocalDateTime endDate, Pageable page);

    @Query("select b from Booking as b join b.item as i where i.owner = ?1 and b.start > ?2")
    Page<Booking> findByOwnerIdAndStartAfter(Long ownerId, LocalDateTime startDate, Pageable page);

    @Query("select b from Booking as b join b.item as i where i.owner = ?1 and b.start < ?2 and b.end > ?2")
    Page<Booking> findByOwnerIdAndTimeCurrent(Long ownerId, LocalDateTime currentDate, Pageable page);

    @Query("select b from Booking as b join b.user as u where u.id = ?1 and b.end < ?2")
    Page<Booking> findByUserIdAndEndBefore(Long userId, LocalDateTime endDate, Pageable page);

    @Query("select b from Booking as b join b.user as u where u.id = ?1 and b.start > ?2")
    Page<Booking> findByUserIdAndStartAfter(Long userId, LocalDateTime startDate, Pageable page);

    @Query("select b from Booking as b join b.user as u where u.id = ?1 and b.start < ?2 and b.end > ?2")
    Page<Booking> findByUserIdAndTimeCurrent(Long userId, LocalDateTime currentDate, Pageable page);
}
