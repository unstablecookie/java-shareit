package ru.practicum.shareit.comment.dto;

import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText());
    }

    public static Comment toComment(CommentDto commentDto, Item item, User user) {
        Comment comment = Comment.builder()
                .text((commentDto.getText() != null) ? commentDto.getText() : null)
                .author(user)
                .item(item)
                .created(LocalDateTime.now())
                .build();
        return comment;
    }

    public static CommentDtoFull toCommentDtoFull(Comment comment, String name) {
        CommentDtoFull commentDtoFull = CommentDtoFull.builder()
                .id((comment.getId() != null) ? comment.getId() : null)
                .text((comment.getText() != null) ? comment.getText() : null)
                .authorName((name != null) ? name : null)
                .created((comment.getCreated() != null) ? comment.getCreated() : null)
                .build();
        return commentDtoFull;
    }
}
