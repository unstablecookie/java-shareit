package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequest {
    Long id;
    @NotNull
    String description;
    @NotNull
    User requestor;
    @NotNull
    LocalDateTime created;
}
