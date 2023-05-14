package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class ItemDto {
    Long id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    Boolean available;
    User owner;
    ItemRequest request;
    BookingDto lastBooking;
    BookingDto nextBooking;
    List<CommentDto> comments;

    @Getter
    @Builder
    static class BookingDto {
        Long id;
        LocalDateTime start;
        LocalDateTime end;
        Long bookerId;
    }
}
