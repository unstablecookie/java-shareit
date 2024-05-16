package ru.practicum.shareit.booking.model;

import java.util.List;

public interface EntityBookingsWithState {
    List<Booking> getEntityBookingsWithState(Long id, Status status);
}
