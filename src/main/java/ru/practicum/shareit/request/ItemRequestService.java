package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto saveItemRequest(ItemRequestDto itemRequestDto, Long userId);

    ItemRequestDto findItemRequestById(Long userId, Long requestId);

    List<ItemRequestDto> findAllSizeFromRequestId(Long userId, Long from, Long size);

    List<ItemRequestDto> findAllByRequestorId(Long userId);

}
