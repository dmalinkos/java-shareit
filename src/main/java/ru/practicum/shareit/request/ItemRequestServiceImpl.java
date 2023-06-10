package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemRequestDto saveItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        User requestor = UserMapper.toUser(userService.findById(userId));
        ItemRequest itemRequestFromDto = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequestFromDto.setCreated(LocalDateTime.now());
        itemRequestFromDto.setRequestor(requestor);
        ItemRequest savedItemRequest = requestRepository.save(itemRequestFromDto);
        return ItemRequestMapper.toItemRequestDto(savedItemRequest);
    }

    @Override
    public ItemRequestResponseDto findItemRequestById(Long userId, Long requestId) {
        userService.checkIfUserExists(userId);
        List<ItemRequestResponseDto.ItemDto> itemsCreatedOnRequest = itemRepository.findByRequestId(requestId)
                .stream()
                .map(item -> ItemRequestResponseDto.ItemDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .owner(item.getOwner().getId())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .requestId(item.getRequest().getId())
                        .build())
                .collect(Collectors.toList());

        return requestRepository.findById(requestId)
                .map(request -> ItemRequestMapper.toItemRequestResponseDto(request, itemsCreatedOnRequest))
                .orElseThrow(() -> new EntityNotExistException(String.format("ItemRequest with id=%d is not exist", requestId)));
    }

    @Override
    public List<ItemRequestResponseDto> findAllSizeFromRequestId(Long userId, Long from, Long size) {
        userService.checkIfUserExists(userId);
        List<ItemRequest> requests = requestRepository.findByRequestorIdNot(userId,
                PageRequest.of(Math.toIntExact(from / size),
                        Math.toIntExact(size),
                        Sort.by("created").ascending()));
        List<Long> requestsIds = requests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        Map<Long, List<Item>> itemsCreatedOnRequests = itemRepository.findByRequestIdIn(requestsIds).stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));
        if (itemsCreatedOnRequests.isEmpty()) {
            return requests.stream()
                    .map(request -> ItemRequestMapper.toItemRequestResponseDto(
                            request, Collections.emptyList()))
                    .collect(Collectors.toList());
        }
        return requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestResponseDto(
                        request,
                        itemsCreatedOnRequests.get(request.getId()).stream()
                                .map(item -> ItemRequestResponseDto.ItemDto.builder()
                                        .id(item.getId())
                                        .name(item.getName())
                                        .owner(item.getOwner().getId())
                                        .description(item.getDescription())
                                        .available(item.getAvailable())
                                        .requestId(item.getRequest().getId())
                                        .build()).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestResponseDto> findAllByRequestorId(Long userId) {
        userService.checkIfUserExists(userId);
        List<ItemRequest> requests = requestRepository.findByRequestorIdOrderByCreatedAsc(userId);
        List<Long> requestsIds = requests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        Map<Long, List<Item>> itemsCreatedOnRequests = itemRepository.findByRequestIdIn(requestsIds).stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));
        if (itemsCreatedOnRequests.isEmpty()) {
            return requests.stream()
                    .map(request -> ItemRequestMapper.toItemRequestResponseDto(
                            request, Collections.emptyList()))
                    .collect(Collectors.toList());
        }
        return requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestResponseDto(
                        request,
                        itemsCreatedOnRequests.get(request.getId()).stream()
                                .map(item -> ItemRequestResponseDto.ItemDto.builder()
                                        .id(item.getId())
                                        .name(item.getName())
                                        .owner(item.getOwner().getId())
                                        .description(item.getDescription())
                                        .available(item.getAvailable())
                                        .requestId(item.getRequest().getId())
                                        .build()).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}
