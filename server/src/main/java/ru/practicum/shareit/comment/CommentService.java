package ru.practicum.shareit.comment;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoFull;

public interface CommentService {
    CommentDtoFull addItemComment(Long userId, CommentDto commentDto, Long itemId);
}
