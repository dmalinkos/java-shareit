package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingDto save(BookingRequestDto bookingRequestDto, Long userId);

    BookingDto update(Long userId, Long bookingId, Boolean approved);

    BookingDto findById(Long bookingId, Long userId);

    List<BookingDto> findAllByState(BookingState state, Long userId);

    List<BookingDto> findAllByOwner(BookingState state, Long userId);

}
