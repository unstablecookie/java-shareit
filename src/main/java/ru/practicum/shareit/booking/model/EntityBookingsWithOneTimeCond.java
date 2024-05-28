package ru.practicum.shareit.booking.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface EntityBookingsWithOneTimeCond {
    Page<Booking> getEntityBookingsWithTimeCon(Long id, LocalDateTime time, Pageable page);
}
