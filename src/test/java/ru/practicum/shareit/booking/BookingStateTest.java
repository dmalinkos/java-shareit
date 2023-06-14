package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BookingStateTest {

    @Test
    void from_whenInvalidString_thenNull() {
        BookingState current = BookingState.from("Current");
        assertNull(current);
    }

    @Test
    void from_whenValidString_thenCURRENT() {
        BookingState current = BookingState.from("CURRENT");
        assertEquals(BookingState.CURRENT, current);
    }
}