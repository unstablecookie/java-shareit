package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.CustomEmailValidator;

@Data
@Builder
@AllArgsConstructor
public class User {
    Long id;
    String name;
    @CustomEmailValidator
    String email;
}
