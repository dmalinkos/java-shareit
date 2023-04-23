package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto itemDto, Long userId);

    ItemDto patch(ItemDto itemDto, Long userId, Long itemId);

    ItemDto findById(Long itemId);

    List<ItemDto> findAllByOwner(Long userId);

    List<ItemDto> search(String text);
}
