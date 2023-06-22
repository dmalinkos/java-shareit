package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Value
@Builder
public class ItemRequestDto {
    Long id;
    @NotBlank(message = "Request description can not be blank", groups = Create.class)
    String description;
    User requestor;
    LocalDateTime created;
}
