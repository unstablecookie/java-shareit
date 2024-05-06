package ru.practicum.shareit.comment;

import ru.practicum.shareit.comment.model.Comment;

public interface CommentService {
    Comment addComment(Long userId, Long bookingId, Comment comment);
}
