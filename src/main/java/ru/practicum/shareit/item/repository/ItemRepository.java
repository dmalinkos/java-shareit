package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

public interface ItemRepository {
    Item save(@Valid Item item, Long userId);

    Item findById(Long itemId);

    List<Item> findAllByOwner(Long userId);

    Item patch(Item item, Long itemId, Long userId);

    List<Item> search(String text);

    Item delete(Long itemId);

}
