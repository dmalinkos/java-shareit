package ru.practicum.shareit.item.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.exception.IncorrectOwnerException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long generatedId = 1L;

    private final String ITEM_NOT_EXIST_MSG = "Item with 'id = %d' is not exist";
    private final String INCORRECT_ITEM_OWNER = "User with 'id = %d is not owner of item with 'id = %d'";

    @Override
    public Item save(Item item, Long userId) {
        item.setId(generateId());
        item.setOwner(userId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item findById(Long itemId) {
        isExist(itemId);
        return items.get(itemId);
    }

    @Override
    public List<Item> findAllByOwner(Long userId) {
        return items.values().stream()
                .filter(v -> v.getOwner().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream()
                .filter(t -> t.getAvailable().equals(Boolean.TRUE))
                .filter(t -> (t.getName().toLowerCase().contains(text.toLowerCase())
                        || t.getDescription().toLowerCase().contains(text.toLowerCase())))
                .collect(Collectors.toList());
    }

    @Override
    public Item patch(Item item, Long itemId, Long userId) {
        isExist(itemId);
        Item patchedItem = items.get(itemId);
        if (!Objects.equals(patchedItem.getOwner(), userId)) {
            throw new IncorrectOwnerException(String.format(INCORRECT_ITEM_OWNER, userId, itemId));
        }
        if (item.getName() != null) {
            patchedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            patchedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            patchedItem.setAvailable(item.getAvailable());
        }
        items.put(itemId, patchedItem);
        return patchedItem;
    }

    @Override
    public Item delete(Long itemId) {
        isExist(itemId);
        return items.remove(itemId);
    }

    private Long generateId() {
        return generatedId++;
    }

    public void isExist(Long itemId) {
        if (!items.containsKey(itemId)) throw new EntityNotExistException(String.format(ITEM_NOT_EXIST_MSG, itemId));
    }
}
