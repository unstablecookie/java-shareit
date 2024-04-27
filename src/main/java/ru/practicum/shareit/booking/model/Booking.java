package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
public class Booking {
    Long id;
    @NotNull
    LocalDate start;
    @NotNull
    LocalDate end;
    @NotNull
    Item item;
    @NotNull
    User booker;
    @NotNull
    Status status;
}
