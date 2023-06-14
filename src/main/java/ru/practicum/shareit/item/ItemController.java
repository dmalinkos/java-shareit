package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto post(@RequestHeader("X-Sharer-User-Id") Long userId,
                        @Valid @RequestBody ItemDto itemDto) {
        return itemService.save(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postItemComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long itemId,
                                      @RequestBody @Validated(value = Create.class) CommentDto commentDto) {
        CommentDto commentDto1 = itemService.addComment(userId, itemId, commentDto);
        return commentDto1;
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @PathVariable Long itemId,
                             @RequestBody ItemDto itemDto) {
        return itemService.patch(itemDto, userId, itemId);
    }

    @GetMapping
    public List<ItemDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                        @RequestParam(defaultValue = "10") @Positive Long size) {
        return itemService.findAllByOwner(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @PathVariable Long itemId) {
        return itemService.findById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                @RequestParam(defaultValue = "10") @Positive Long size) {
        return itemService.search(text, from, size);
    }
}

