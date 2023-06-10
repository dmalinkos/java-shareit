package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto itemDto, Long userId);

    ItemDto patch(ItemDto itemDto, Long userId, Long itemId);

    ItemDto findById(Long itemId, Long userId);

    List<ItemDto> findAllByOwner(Long userId, Long from, Long size);

    List<ItemDto> search(String text, Long from, Long size);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
