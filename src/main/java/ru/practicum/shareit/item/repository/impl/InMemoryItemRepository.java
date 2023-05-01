package ru.practicum.shareit.item.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private Long generatedId = 1L;

    @Override
    public Item save(Item item, Long userId) {
        item.setId(generateId());
        item.setOwner(userId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        if (checkIfItemExists(itemId)) {
            return Optional.of(items.get(itemId));
        } else {
            return Optional.empty();
        }
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
    public Optional<Item> patch(Item item, Long itemId, Long userId) {
        checkIfItemExists(itemId);
        Item patchedItem = items.get(itemId);
        if (!Objects.equals(patchedItem.getOwner(), userId)) {
            return Optional.empty();
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
        return Optional.of(patchedItem);
    }

    @Override
    public Item delete(Long itemId) {
        checkIfItemExists(itemId);
        return items.remove(itemId);
    }

    private Long generateId() {
        return generatedId++;
    }

    public boolean checkIfItemExists(Long itemId) {
        return items.containsKey(itemId);
    }
}
