package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public ItemRequestDto saveItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        userService.checkIfUserExists(userId);
        ItemRequest itemRequestFromDto = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequestFromDto.setCreated(LocalDateTime.now());
        ItemRequest savedItemRequest = requestRepository.save(itemRequestFromDto);
        return ItemRequestMapper.toItemRequestDto(savedItemRequest);
    }

    @Override
    public ItemRequestDto findItemRequestById(Long userId, Long requestId) {
        userService.checkIfUserExists(userId);
        return requestRepository.findById(requestId)
                .map(ItemRequestMapper::toItemRequestDto)
                .orElseThrow(() -> new EntityNotExistException(String.format("ItemRequest with id=%d is not exist", requestId)));
    }

    @Override
    public List<ItemRequestDto> findAllSizeFromRequestId(Long userId, Long from, Long size) {
        userService.checkIfUserExists(userId);
        return requestRepository.findAllByRequestorId(userId,
                        PageRequest.of(Math.toIntExact(from / size),
                                Math.toIntExact(size),
                                Sort.by(Sort.Direction.DESC, "created"))).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> findAllByRequestorId(Long userId) {
        return null;
    }
}
