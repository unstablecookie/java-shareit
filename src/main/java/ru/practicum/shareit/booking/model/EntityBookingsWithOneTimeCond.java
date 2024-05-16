package ru.practicum.shareit.booking.model;

import java.time.LocalDateTime;
import java.util.List;

public interface EntityBookingsWithOneTimeCond {
    List<Booking> getEntityBookingsWithTimeCon(Long id, LocalDateTime time);
}
