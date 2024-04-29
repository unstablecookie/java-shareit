package ru.practicum.shareit.comment.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class Comment {
    private Long id;
    private Long itemId;
    @NotNull
    private String text;
}
