package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item save(@Valid Item item, Long userId);

    Optional<Item> findById(Long itemId);

    List<Item> findAllByOwner(Long userId);

    Optional<Item> patch(Item item, Long itemId, Long userId);

    List<Item> search(String text);

    Item delete(Long itemId);

}
