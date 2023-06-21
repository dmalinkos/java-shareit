package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    @MockBean
    private BookingService bookingService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingRequestDto bookingRequestDto;
    private BookingDto bookingDto;
    private final Long bookingId = 1L;
    private Item item;
    private final Long ownerId = 1L;
    private User booker;
    private final Long bookerId = 2L;

    @BeforeEach
    void setup() {

        String ownerName = "ownerName";
        String ownerEmail = "ownerEmail";

        User owner = User.builder()
                .id(ownerId)
                .name(ownerName)
                .email(ownerEmail)
                .build();

        String bookerName = "bookerName";
        String bookerEmail = "bookerEmail";

        booker = User.builder()
                .id(bookerId)
                .name(bookerName)
                .email(bookerEmail)
                .build();

        String itemName = "itemName";
        String itemDescription = "itemDescription";

        Long itemId = 1L;
        item = Item.builder()
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

        bookingDto = BookingDto.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

    }

    @SneakyThrows
    @Test
    void save_whenInputValid_thenBookingDto() {
        when(bookingService.save(bookingRequestDto, booker.getId())).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header(SHARER_USER_ID_HEADER, bookerId)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
        verify(bookingService).save(bookingRequestDto, bookerId);
    }

//    @SneakyThrows
//    @Test
//    void save_whenInputInvalid_thenConstraintViolationException() {
//        bookingRequestDto = BookingRequestDto.builder()
//                .itemId(null)
//                .start(start.minusDays(1))
//                .end(end.minusMinutes(1))
//                .build();
//
//        mvc.perform(post("/bookings")
//                        .header(SHARER_USER_ID_HEADER, 2L)
//                        .content(mapper.writeValueAsString(bookingRequestDto))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//
//                .andExpect(status().isBadRequest());
//        verify(bookingService, never()).save(any(), any());
//    }

    @SneakyThrows
    @Test
    void update_whenInputValid_thenBookingDto() {
        bookingDto = BookingDto.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingService.update(eq(ownerId), eq(bookingId), anyBoolean())).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", "true")
                        .header(SHARER_USER_ID_HEADER, ownerId)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpectAll(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
        verify(bookingService).update(ownerId, bookingId, true);
    }

    @SneakyThrows
    @Test
    void findAllByState_whenInputValid_thenListOfBookingDto() {
        when(bookingService.findAllByState(any(), anyLong(), anyLong(), anyLong())).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header(SHARER_USER_ID_HEADER, bookerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));

    }

//    @SneakyThrows
//    @Test
//    void findAllByState_whenInvalidFromParam_thenConstraintViolationException() {
//
//        mvc.perform(get("/bookings")
//                        .param("from", "-1")
//                        .header(SHARER_USER_ID_HEADER, bookerId)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//
//                .andExpect(status().isBadRequest());
//
//    }

    @SneakyThrows
    @Test
    void findById_whenInputValid_thenBookingDto() {

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(SHARER_USER_ID_HEADER, bookerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk());
    }

//    @SneakyThrows
//    @Test
//    void findAllByState_whenInvalidSizeParam_thenConstraintViolationException() {
//
//        mvc.perform(get("/bookings")
//                        .param("size", "-1")
//                        .header(SHARER_USER_ID_HEADER, bookerId)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//
//                .andExpect(status().isBadRequest());
//
//    }

    @SneakyThrows
    @Test
    void findAllByOwner_whenInputValid_thenListOfBookingDto() {
        when(bookingService.findAllByOwner(any(), anyLong(), anyLong(), anyLong())).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header(SHARER_USER_ID_HEADER, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));

    }

//    @SneakyThrows
//    @Test
//    void findAllByOwner_whenInvalidFromParam_thenConstraintViolationException() {
//
//        mvc.perform(get("/bookings/owner")
//                        .param("from", "-1")
//                        .header(SHARER_USER_ID_HEADER, bookerId)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//
//                .andExpect(status().isBadRequest());
//
//    }

//    @SneakyThrows
//    @Test
//    void findAllByOwner_whenInvalidSizeParam_thenConstraintViolationException() {
//
//        mvc.perform(get("/bookings/owner")
//                        .param("size", "-1")
//                        .header(SHARER_USER_ID_HEADER, bookerId)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//
//                .andExpect(status().isBadRequest());
//
//    }

}