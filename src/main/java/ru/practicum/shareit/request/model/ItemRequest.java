package ru.practicum.shareit.request.model;

import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ItemRequest {
    @NotNull
    private Long id;
    @NotBlank
    private String description;
    @NotNull
    private User requestor;
    private LocalDateTime created;
}
