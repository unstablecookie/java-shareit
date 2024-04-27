package ru.practicum.shareit.comment;

import ru.practicum.shareit.comment.model.Comment;

public interface CommentRepository {
    Comment addComment(Comment comment);
}
