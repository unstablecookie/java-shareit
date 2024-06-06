package ru.practicum.shareit.booking.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EntityBookings {
    Page<Booking> getEntityBookingsOrderByStartDesc(Long id, Pageable page);
}
