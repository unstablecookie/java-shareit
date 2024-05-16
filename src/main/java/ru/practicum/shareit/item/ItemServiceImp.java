package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.error.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImp implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("user id: %d was not found", userId)));
        Item item = ItemMapper.toItem(userId, itemDto);
        ItemDto newItemDto = ItemMapper.toItemDto(itemRepository.save(item));
        return newItemDto;
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("user id: %d was not found", userId)));
        Item oldItem = itemRepository.findById(itemId).orElseThrow(
                () -> new EntityNotFoundException(String.format("item id: %d was not found", itemId)));
        Item item = ItemMapper.toItem(userId, itemDto);
        item.setId(itemId);
        if (validateOwner(userId, item)) {
            throw new EntityNotFoundException(String.format("user id: %d is not an owner", userId));
        }
        Item updatedItem = ItemMapper.updateItemWithItem(oldItem, item);
        itemRepository.save(updatedItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemWithBookingsDto getItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new EntityNotFoundException(String.format("item id: %d was not found", itemId)));
        if (userId.equals(item.getOwner())) {
            return itemWithBookingsDtoUpdater(item);
        } else {
            ItemWithBookingsDto itemWithBookingsDto = ItemMapper.toItemWithBookingsDto(item);
            updateComments(itemWithBookingsDto, itemId);
            return itemWithBookingsDto;
        }
    }

    @Override
    public List<ItemWithBookingsDto> getUserItems(Long userId) {
        userRepository.findById(userId);
        Set<Item> items = itemRepository.findByOwner(userId).stream().collect(Collectors.toSet());
        return items.stream()
                .map(x -> itemWithBookingsDtoUpdater(x))
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String searchText) {
        if (searchText.length() < 1) {
            return List.of();
        }
        List<Item> items = itemRepository.findByDescriptionContainingIgnoreCase(searchText.toLowerCase()).stream()
                .filter(x -> x.getAvailable().equals(Boolean.TRUE))
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .collect(Collectors.toList());
        return ItemMapper.allItemsToItemsDto(items);
    }

    @Override
    public void deleteItem(Long itemId) {
        List<Booking> bookings = bookingRepository.findByItemIdOrderByStartDesc(itemId);
        bookingRepository.deleteAllInBatch(bookings);
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new EntityNotFoundException(String.format("item id %d not found", itemId));
        }
        itemRepository.delete(item.get());
    }

    private boolean validateOwner(Long userId, Item item) {
        Optional<Item> oldItem = itemRepository.findById(item.getId());
        if (oldItem.isEmpty()) {
            throw new EntityNotFoundException(String.format("item id %d not found", item.getId()));
        }
        return !oldItem.get().getOwner().equals(userId);
    }

    private ItemWithBookingsDto itemWithBookingsDtoUpdater(Item item) {
        ItemWithBookingsDto itemWithBookingsDto = ItemMapper.toItemWithBookingsDto(item);
        List<Booking> bookings = bookingRepository.findByItemIdOrderByStartDesc(item.getId()).stream()
                .filter(x -> !x.getStatus().equals(Status.REJECTED))
                .sorted((a, b) -> a.getStart().compareTo(b.getStart()))
                .collect(Collectors.toList());
        Booking lastBooking = bookings.stream()
                .filter(x -> x.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);
        if (lastBooking != null) {
            itemWithBookingsDto.setLastBooking(BookingMapper.toMinBookingDto(lastBooking));
        }
        Booking nextBooking = bookings.stream()
                .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getEnd))
                .orElse(null);
        if (nextBooking != null) {
            itemWithBookingsDto.setNextBooking(BookingMapper.toMinBookingDto(nextBooking));
        }
        updateComments(itemWithBookingsDto, item.getId());
        return itemWithBookingsDto;
    }

    private void updateComments(ItemWithBookingsDto itemWithBookingsDto, Long itemId) {
        List<Comment> comments = commentRepository.findByItemId(itemId);
        itemWithBookingsDto.setComments(comments.stream()
                .map(x -> CommentMapper.toCommentDtoFull(x, x.getAuthor().getName()))
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .collect(Collectors.toList()));
    }
}
