package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.Create;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @Validated(value = Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.saveItemRequest(itemRequestDto, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable(name = "requestId") Long requestId) {
        return itemRequestService.findItemRequestById(userId, requestId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.findAllByRequestorId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getALlRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(name = "from") Long from,
                                               @RequestParam(name = "size") Long size) {
        return itemRequestService.findAllSizeFromRequestId(userId, from, size);
    }
}
