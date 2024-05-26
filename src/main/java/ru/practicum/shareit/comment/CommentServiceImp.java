package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoFull;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.error.EntityNotFoundException;
import ru.practicum.shareit.error.UnsupportedStatusException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImp implements CommentService {
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public CommentDtoFull addItemComment(Long userId, CommentDto commentDto, Long itemId) {
        List<Booking> bookings = bookingRepository.findUsersBookingForAnItemOrderByStartDesc(userId, itemId).stream()
                .filter(x -> !x.getStatus().equals(Status.REJECTED))
                .filter(x -> x.getStart().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        if (bookings.size() < 1) {
            throw new UnsupportedStatusException();
        }
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("user id: %d was not found", userId)));
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new EntityNotFoundException(String.format("item id: %d was not found", itemId)));
        Comment createdComment = commentRepository.save(CommentMapper.toComment(commentDto, item, user));
        return CommentMapper.toCommentDtoFull(createdComment, user.getName());
    }
}
