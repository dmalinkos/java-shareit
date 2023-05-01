package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class Item {
    @NotNull private Long id;
    @NotBlank private String name;
    @NotBlank private String description;
    @NotNull private Boolean available;
    @NotNull private Long owner;
    private ItemRequest request;
}
