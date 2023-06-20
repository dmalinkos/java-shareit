package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestMapperTest {
    private UserDto requestorDto;
    private User owner;
    private User requestor;
    private Long requestorId = 1L;
    private ItemRequestDto itemRequestDto;
    private ItemRequest savedItemRequest;
    private ItemRequestDto savedItemRequestDto;
    private ItemRequestResponseDto itemRequestResponseDto;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setup() {
        requestor = User.builder()
                .id(requestorId)
                .name("requestor")
                .email("email@ya.ru")
                .build();
        requestorDto = UserDto.builder()
                .id(requestorId)
                .name("requestor")
                .email("email@ya.ru")
                .build();
        savedItemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .requestor(requestor)
                .description("desc")
                .build();
        savedItemRequest = ItemRequest.builder()
                .id(1L)
                .requestor(requestor)
                .description("desc")
                .build();
        owner = User.builder()
                .id(2L)
                .name("owner")
                .email("emailOwner@ya.ru")
                .build();
        item = Item.builder()
                .id(1L)
                .owner(owner)
                .name("name")
                .description("desc")
                .request(savedItemRequest)
                .available(true)
                .build();
        itemDto = ItemDto.builder()
                .id(1L)
                .owner(owner)
                .name("name")
                .description("desc")
                .requestId(savedItemRequest.getId())
                .available(true)
                .build();
        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("desc")
                .requestor(requestor)
                .build();
        itemRequestResponseDto = ItemRequestResponseDto.builder()
                .id(1L)
                .items(List.of(ItemRequestResponseDto.ItemDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .owner(item.getOwner().getId())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .requestId(item.getRequest().getId())
                        .build()))
                .description("desc")
                .build();
    }

    @Test
    void toItemRequest() {
        ItemRequest mappedRequestDto = ItemRequestMapper.toItemRequest(itemRequestDto);

        assertEquals(savedItemRequest.toString(), mappedRequestDto.toString());
    }

    @Test
    void toItemRequestResponseDto() {
        ItemRequestResponseDto testItemRequestResponseDto = ItemRequestMapper.toItemRequestResponseDto(
                savedItemRequest,
                List.of(ItemRequestResponseDto.ItemDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .owner(item.getOwner().getId())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .requestId(item.getRequest().getId())
                        .build()));

        assertEquals(itemRequestResponseDto.toString(), testItemRequestResponseDto.toString());
    }

    @Test
    void toItemRequestDto() {
        ItemRequestDto testItemRequestDto = ItemRequestMapper.toItemRequestDto(savedItemRequest);

        assertEquals(savedItemRequestDto.toString(), testItemRequestDto.toString());
    }
}