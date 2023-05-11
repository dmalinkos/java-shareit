package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.exception.UnavailableError;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private static final String INCORRECT_ITEM_OWNER = "User with 'id = %d is not owner of item with 'id = %d'";
    private static final String BOOKING_NOT_EXIST_MSG = "Booking with id=%d is not exist";
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public BookingDto save(@Valid BookingRequestDto bookingDto, Long userId) {
        User user = UserMapper.toUser(userService.findById(userId));
        Item item = ItemMapper.toItem(itemService.findById(bookingDto.getItemId(), userId));
        Booking booking = BookingMapper.toBooking(bookingDto);
        validateBooking(booking, item, userId);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public BookingDto update(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotExistException(
                        String.format(BOOKING_NOT_EXIST_MSG, bookingId)));
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new EntityNotExistException(String.format(INCORRECT_ITEM_OWNER, userId, booking.getItem().getId()));
        }
        if (booking.getStatus().equals(BookingStatus.WAITING)) {
            booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        } else {
            throw new BadRequestException(String.format("Booking with id=%d already APPROVED or REJECTED", bookingId));
        }
        Booking updatedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(updatedBooking);
    }

    @Override
    public BookingDto findById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotExistException(String.format(BOOKING_NOT_EXIST_MSG, bookingId)));
        if (!Objects.equals(userId, booking.getBooker().getId())
                && !Objects.equals(userId, booking.getItem().getOwner().getId())) {
            throw new EntityNotExistException(String.format("User with id=%d not owner Item with id=%d or not booker Booking with id=%d",
                    userId, booking.getItem().getId(),
                    booking.getBooker().getId()));
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findAllByState(BookingState state, Long userId) {
        userService.findById(userId);
        List<Booking> bookings;
        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllByOwner(BookingState state, Long userId) {
        userService.findById(userId);
        List<Booking> bookings;
        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private void validateBooking(Booking booking, Item item, Long bookerId) {
        if (Objects.equals(bookerId, item.getId())) {
            throw new EntityNotExistException(String.format("User with id=%d is owner Item with id=%d", bookerId, item.getId()));
        }
        if (!item.getAvailable()) {
            throw new UnavailableError(String.format("Item with id=%d is unavailable", item.getId()));
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new BadRequestException(String.format("StartTime: %s is after EndTime: %s", booking.getStart(), booking.getEnd()));
        }
        if (booking.getStart().isEqual(booking.getEnd())) {
            throw new BadRequestException(String.format("StartTime: %s is equal EndTime: %s", booking.getStart(), booking.getEnd()));
        }

    }
}
