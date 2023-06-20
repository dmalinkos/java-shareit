package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
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

        itemRequestDto = ItemRequestDto.builder()
                .description("desc")
                .build();
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
    void saveItemRequest_whenValidInput_thenSavedItemRequestDto() {

        when(userService.findById(requestorId)).thenReturn(requestorDto);
        when(requestRepository.save(any())).thenAnswer(invocationOnMock -> {
                    ItemRequest itemRequest = invocationOnMock.getArgument(0, ItemRequest.class);
                    itemRequest.setId(1L);
                    return itemRequest;
                }
        );

        ItemRequestDto testItemRequestDto = itemRequestService.saveItemRequest(itemRequestDto, requestorId);

        assertEquals(savedItemRequestDto.getId(), testItemRequestDto.getId());
        assertEquals(savedItemRequestDto.getDescription(), testItemRequestDto.getDescription());
        assertEquals(savedItemRequestDto.getRequestor(), testItemRequestDto.getRequestor());
        verify(userService, only()).findById(anyLong());
        verify(requestRepository, only()).save(any());

    }

    @Test
    void findItemRequestById_whenInputValid_thenItemRequestResponseDto() {

        doNothing().when(userService).checkIfUserExists(anyLong());
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));
        when(requestRepository.findById(1L)).thenReturn(Optional.of(savedItemRequest));

        ItemRequestResponseDto testItemRequestResponseDto = itemRequestService.findItemRequestById(1L, 1L);

        assertEquals(itemRequestResponseDto.toString(), testItemRequestResponseDto.toString());
        verify(userService, only()).checkIfUserExists(anyLong());
        verify(itemRepository, only()).findByRequestId(anyLong());
        verify(requestRepository, only()).findById(anyLong());

    }

    @Test
    void findItemRequestById_whenInvalidRequestId_thenEntityNotExistException() {

        doNothing().when(userService).checkIfUserExists(anyLong());
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotExistException.class,
                () -> itemRequestService.findItemRequestById(1L, 1L));
        verify(userService, only()).checkIfUserExists(anyLong());
        verify(itemRepository, only()).findByRequestId(anyLong());
        verify(requestRepository, only()).findById(anyLong());

    }

    @Test
    void findAllSizeFromRequestId_whenInputValid_thenListOfItemRequestResponseDto() {

        doNothing().when(userService).checkIfUserExists(anyLong());
        when(requestRepository.findByRequestorIdNot(anyLong(), any(Pageable.class))).thenReturn(List.of(savedItemRequest));
        when(itemRepository.findByRequestIdIn(anyList())).thenReturn(List.of(item));

        List<ItemRequestResponseDto> itemRequestResponseDtos = itemRequestService.findAllSizeFromRequestId(anyLong(), 0L, 10L);

        assertEquals(itemRequestResponseDto.toString(), itemRequestResponseDtos.get(0).toString());
        verify(userService, only()).checkIfUserExists(anyLong());
        verify(requestRepository, only()).findByRequestorIdNot(anyLong(), any());
        verify(itemRepository, only()).findByRequestIdIn(anyList());
    }

}