package ru.practicum.shareit.comment;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.comment.model.Comment;

import java.util.*;

@Repository
public class CommentRepositoryInMemory implements CommentRepository {
    private final Map<Long, Set<Comment>> storage = new HashMap<>();
    private Long counter = 1L;

    @Override
    public Comment addComment(Comment comment) {
        comment.setId(counter);
        counter++;
        if (storage.get(comment.getItemId()) == null) {
            Set<Comment> comments = new HashSet<>();
            comments.add(comment);
        } else {
            Set<Comment> comments = storage.get(comment.getItemId());
            comments.add(comment);
        }
        return comment;
    }
}
