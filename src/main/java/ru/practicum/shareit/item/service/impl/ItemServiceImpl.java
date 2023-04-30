package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.exception.IncorrectOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.util.ItemMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private static final String ITEM_NOT_EXIST_MSG = "Item with 'id = %d' is not exist";
    private static final String INCORRECT_ITEM_OWNER = "User with 'id = %d is not owner of item with 'id = %d'";

    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        userService.checkIfUserExists(userId);
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto), userId));
    }

    @Override
    public ItemDto patch(ItemDto itemDto, Long userId, Long itemId) {
        userService.checkIfUserExists(userId);
        Optional<Item> optionalItem = itemRepository.patch(ItemMapper.toItem(itemDto), itemId, userId);
        return optionalItem
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new IncorrectOwnerException(String.format(INCORRECT_ITEM_OWNER, userId, itemId)));
    }

    @Override
    public ItemDto findById(Long itemId) {
        return itemRepository.findById(itemId)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new EntityNotExistException(String.format(ITEM_NOT_EXIST_MSG, itemId)));
    }

    @Override
    public List<ItemDto> findAllByOwner(Long userId) {
        return itemRepository.findAllByOwner(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
