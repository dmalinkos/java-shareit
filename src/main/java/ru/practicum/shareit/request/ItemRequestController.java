package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.saveItemRequest(itemRequestDto, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable(name = "requestId") Long requestId) {
        return itemRequestService.findItemRequestById(userId, requestId);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getAllUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.findAllByRequestorId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getALlRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                                       @RequestParam(defaultValue = "10") @Positive Long size) {
        return itemRequestService.findAllSizeFromRequestId(userId, from, size);
    }
}
