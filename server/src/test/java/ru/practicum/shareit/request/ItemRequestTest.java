package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ItemRequestTest {

    @Test
    void testEquals() {
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("desc")
                .build();
        ItemRequest request1 = ItemRequest.builder()
                .id(1L)
                .description("desc")
                .build();
        ItemRequest request2 = ItemRequest.builder()
                .id(2L)
                .description("desc")
                .build();

        assertEquals(request, request1);
        assertNotEquals(request, request2);
    }
}