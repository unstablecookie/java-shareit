package ru.practicum.shareit.comment.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class Comment {
    Long id;
    Long itemId;
    @NotNull
    String text;
}
