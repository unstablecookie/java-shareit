package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto addBooking(BookingDto bookingDto) {
        userRepository.getUser(bookingDto.getBooker());
        itemRepository.getItem(bookingDto.getItem());
        Item item = itemRepository.getItem(bookingDto.getItem());
        User booker = userRepository.getUser(bookingDto.getBooker());
        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        return BookingMapper.toBookingDto(bookingRepository.addBooking(booking));
    }

    @Override
    public BookingDto updateBooking(Long bookingId, BookingDto bookingDto) {
        Item item = itemRepository.getItem(bookingDto.getItem());
        User booker = userRepository.getUser(bookingDto.getBooker());
        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        return BookingMapper.toBookingDto(bookingRepository.updateBooking(bookingId, booking));
    }

    @Override
    public BookingDto getBooking(Long bookingId) {
        Booking booking = bookingRepository.getBooking(bookingId);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public void deleteBooking(Long bookingId) {
        bookingRepository.deleteBooking(bookingId);
    }

    @Override
    public void approveBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.getBooking(bookingId);
        if ((booking == null) || (booking.getItem().getOwner() != userId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, String.format("user id %d is not owner or booking does not exist",
                    userId, bookingId)
            );
        }
        booking.setStatus(Status.APPROVED);
        bookingRepository.updateBooking(bookingId, booking);
    }

    @Override
    public void rejectBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.getBooking(bookingId);
        if ((booking == null) || (booking.getItem().getOwner() != userId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, String.format("user id %d is not owner or booking does not exist",
                    userId, bookingId)
            );
        }
        booking.setStatus(Status.REJECTED);
        bookingRepository.updateBooking(bookingId, booking);
    }

    @Override
    public void cancelBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.getBooking(bookingId);
        if ((booking == null) || (booking.getBooker().getId() != userId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, String.format("user id %d is not owner or booking does not exist",
                    userId, bookingId)
            );
        }
        booking.setStatus(Status.CANCELED);
        bookingRepository.updateBooking(bookingId, booking);
    }
}
