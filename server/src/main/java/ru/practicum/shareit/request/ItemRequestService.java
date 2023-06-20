package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto saveItemRequest(ItemRequestDto itemRequestDto, Long userId);

    ItemRequestResponseDto findItemRequestById(Long userId, Long requestId);

    List<ItemRequestResponseDto> findAllSizeFromRequestId(Long userId, Long from, Long size);

    List<ItemRequestResponseDto> findAllByRequestorId(Long userId);

}
