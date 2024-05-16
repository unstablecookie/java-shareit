package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    @Query("select b from Booking as b join b.user as u join b.item as i where u.id = ?1 and i.id = ?2")
    List<Booking> findUsersBookingForAnItemOrderByStartDesc(Long userId, Long itemId);

    @Query("select b from Booking as b join b.item as i where i.id = ?1 order by b.start desc")
    List<Booking> findByItemIdOrderByStartDesc(Long itemId);

    @Query("select b from Booking as b join b.item as i where i.owner = ?1")
    List<Booking> findAllOwnerBookings(Long userId);

    @Query("select b from Booking as b join b.item as i where i.owner = ?1 and b.status = ?2")
    List<Booking> findAllOwnerBookingsAndStatus(Long userId, Status status);

    List<Booking> findByUserIdAndStatus(Long userId, Status status);

    @Query("select b from Booking as b join b.item as i where i.owner = ?1 and b.end < ?2")
    List<Booking> findByOwnerIdAndEndBefore(Long ownerId, LocalDateTime endDate);

    @Query("select b from Booking as b join b.item as i where i.owner = ?1 and b.start > ?2")
    List<Booking> findByOwnerIdAndStartAfter(Long ownerId, LocalDateTime startDate);

    @Query("select b from Booking as b join b.item as i where i.owner = ?1 and b.start < ?2 and b.end > ?2")
    List<Booking> findByOwnerIdAndTimeCurrent(Long ownerId, LocalDateTime currentDate);

    @Query("select b from Booking as b join b.user as u where u.id = ?1 and b.end < ?2")
    List<Booking> findByUserIdAndEndBefore(Long userId, LocalDateTime endDate);

    @Query("select b from Booking as b join b.user as u where u.id = ?1 and b.start > ?2")
    List<Booking> findByUserIdAndStartAfter(Long userId, LocalDateTime startDate);

    @Query("select b from Booking as b join b.user as u where u.id = ?1 and b.start < ?2 and b.end > ?2")
    List<Booking> findByUserIdAndTimeCurrent(Long userId, LocalDateTime currentDate);
}
