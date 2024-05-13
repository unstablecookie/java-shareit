package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking as b join b.user as u where u.id = ?1 order by b.start desc")
    List<Booking> findUsersBookings(Long userId);

    @Query("select b from Booking as b join b.user as u join b.item as i where u.id = ?1 and i.id = ?2")
    List<Booking> findUsersBookingForAnItemOrderByStartDesc(Long userId, Long itemId);

    @Query("select b from Booking as b join b.item as i where i.id = ?1 order by b.start desc")
    List<Booking> findByItemIdOrderByStartDesc(Long itemId);
}
