package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.util.ItemMapper;
import ru.practicum.shareit.user.repository.impl.InMemoryUserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final InMemoryUserRepository userRepository;

    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        userRepository.isExist(userId);
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto), userId));
    }

    @Override
    public ItemDto patch(ItemDto itemDto, Long userId, Long itemId) {
        userRepository.isExist(userId);
        return ItemMapper.toItemDto(itemRepository.patch(ItemMapper.toItem(itemDto), itemId, userId));
    }

    @Override
    public ItemDto findById(Long itemId) {
        return ItemMapper.toItemDto(itemRepository.findById(itemId));
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
