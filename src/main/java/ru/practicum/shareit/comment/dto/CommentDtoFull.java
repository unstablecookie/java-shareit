package ru.practicum.shareit.comment.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDtoFull {
    Long id;
    String text;
    String authorName;
    LocalDateTime created;
}
