package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class ItemRequestResponseDto {
    Long id;
    String description;
    LocalDateTime created;
    List<ItemDto> items;

    @Builder
    @Getter
    public static class ItemDto {
        private Long id;
        private String name;
        private Long owner;
        private String description;
        private Boolean available;
        private Long requestId;
    }

}