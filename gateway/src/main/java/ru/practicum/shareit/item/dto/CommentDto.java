package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Value
@Builder
public class CommentDto {
    Long id;
    @NotBlank(message = "Comment can not be blank", groups = Create.class)
    String text;
    String authorName;
    @Builder.Default
    LocalDateTime created = LocalDateTime.now();
}
