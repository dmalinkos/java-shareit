package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingRequestDto bookingRequestDto;
    private BookingDto bookingDto;
    private Booking booking;

    @BeforeEach
    void setup() {

        String ownerName = "ownerName";
        String ownerEmail = "ownerEmail";

        Long ownerId = 1L;
        User owner = User.builder()
                .id(ownerId)
                .name(ownerName)
                .email(ownerEmail)
                .build();

        String bookerName = "bookerName";
        String bookerEmail = "bookerEmail";

        Long bookerId = 2L;
        User booker = User.builder()
                .id(bookerId)
                .name(bookerName)
                .email(bookerEmail)
                .build();


        String itemName = "itemName";
        String itemDescription = "itemDescription";

        Long itemId = 1L;
        Item item = Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .owner(owner)
                .available(true)
                .build();

        start = LocalDateTime.now().plusMinutes(1);
        end = start.plusMinutes(1);

        bookingRequestDto = BookingRequestDto.builder()
                .itemId(itemId)
                .start(start)
                .end(end)
                .build();

        Long bookingId = 1L;
        bookingDto = BookingDto.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        booking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

    }

    @Test
    void toBookingDtoFromBooking() {

        BookingDto mappedBooking = BookingMapper.toBookingDto(booking);

        assertEquals(mappedBooking.toString(), bookingDto.toString());

    }

    @Test
    void toBookingFromBookingDto() {

        Booking mappedBookingDto = BookingMapper.toBooking(bookingDto);

        assertEquals(mappedBookingDto.toString(), booking.toString());

    }

    @Test
    void toBookingFromBookingRequestDto() {

        booking = Booking.builder()
                .start(start)
                .end(end)
                .build();

        Booking mappedBookingRequestDto = BookingMapper.toBooking(bookingRequestDto);

        assertEquals(mappedBookingRequestDto.toString(), booking.toString());

    }
}