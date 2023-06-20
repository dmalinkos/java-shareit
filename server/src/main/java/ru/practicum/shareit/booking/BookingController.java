package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto save(@Valid @RequestBody BookingRequestDto bookingRequestDto,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.save(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @PathVariable Long bookingId,
                             @RequestParam Boolean approved) {
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@PathVariable Long bookingId,
                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findAllByState(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String stateParam,
            @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
            @RequestParam(defaultValue = "10") @Positive Long size) {
        BookingState state = bookingStateFromStateParam(stateParam);
        return bookingService.findAllByState(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(name = "state", required = false, defaultValue = "ALL") String stateParam,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                           @RequestParam(defaultValue = "10") @Positive Long size) {
        BookingState state = bookingStateFromStateParam(stateParam);
        return bookingService.findAllByOwner(state, userId, from, size);
    }

    private BookingState bookingStateFromStateParam(String stateParam) {
        BookingState state = BookingState.from(stateParam);
        if (state == null) {
            throw new IllegalArgumentException("Unknown state: " + stateParam);
        }
        return state;
    }
}
