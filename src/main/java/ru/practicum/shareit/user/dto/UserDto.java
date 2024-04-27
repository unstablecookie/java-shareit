package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.CustomEmailValidator;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    Long id;
    String name;
    @CustomEmailValidator
    String email;
}
