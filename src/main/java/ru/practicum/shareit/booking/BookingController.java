package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto save(@Valid @RequestBody BookingRequestDto bookingDto,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.save(bookingDto, userId);
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
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String stateParam) {
        BookingState state = bookingStateFromStateParam(stateParam);
        return bookingService.findAllByState(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(name = "state", required = false, defaultValue = "ALL") String stateParam) {
        BookingState state = bookingStateFromStateParam(stateParam);
        return bookingService.findAllByOwner(state, userId);
    }

    private BookingState bookingStateFromStateParam(String stateParam) {
        BookingState state = BookingState.from(stateParam);
        if (state == null) {
            throw new IllegalArgumentException("Unknown state: " + stateParam);
        }
        return state;
    }
}
