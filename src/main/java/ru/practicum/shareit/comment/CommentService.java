package ru.practicum.shareit.comment;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoFull;

import java.util.Optional;

public interface CommentService {
    Optional<CommentDtoFull> addItemComment(Long userId, CommentDto commentDto, Long itemId);
}
