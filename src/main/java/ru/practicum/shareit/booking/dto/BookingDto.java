package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
public class BookingDto {
    @NotNull
    private LocalDate start;
    @NotNull
    private LocalDate end;
    @NotNull
    private Long item;
    @NotNull
    private Long booker;
    @NotNull
    private Status status = Status.WAITING;
}
