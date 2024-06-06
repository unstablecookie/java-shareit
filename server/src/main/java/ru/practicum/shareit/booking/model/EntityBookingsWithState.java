package ru.practicum.shareit.booking.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EntityBookingsWithState {
    Page<Booking> getEntityBookingsWithState(Long id, Status status, Pageable page);
}
