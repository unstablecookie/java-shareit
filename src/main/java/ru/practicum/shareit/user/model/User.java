package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.CustomEmailValidator;

@Data
@Builder
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    @CustomEmailValidator
    private String email;
}
