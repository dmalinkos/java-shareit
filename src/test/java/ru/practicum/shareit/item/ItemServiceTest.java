package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.exception.IncorrectOwnerException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository requestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    private ItemDto itemDto;
    private Item item;
    private ItemDto savedItemDto;
    private UserDto userDto;
    private User user;
    private User booker;
    private User requestor;
    private ItemRequest request;
    private LocalDateTime created;
    private LocalDateTime start;
    private LocalDateTime end;
    private Booking lastBooking;
    private Booking nextBooking;

    @BeforeEach
    void setup() {
        created = LocalDateTime.now();
        userDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email@ya.ru")
                .build();
        user = User.builder()
                .id(1L)
                .name("name")
                .email("email@ya.ru")
                .build();
        requestor = User.builder()
                .id(2L)
                .name("nameReq")
                .email("req@ya.ru")
                .build();
        itemDto = ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true)
                .build();
        item = Item.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .owner(user)
                .available(true)
                .build();
        savedItemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .owner(user)
                .available(true)
                .build();
        request = ItemRequest.builder()
                .id(1L)
                .description("desc")
                .created(created)
                .requestor(requestor)
                .build();
        booker = User.builder()
                .id(2L)
                .name("booker")
                .email("booker@ya.ru")
                .build();
        start = LocalDateTime.now().minusMinutes(2);
        end = start.plusMinutes(1);
        lastBooking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        nextBooking = Booking.builder()
                .id(2L)
                .start(start.plusMinutes(10))
                .end(end.plusMinutes(10))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void save_whenInputValidWithoutRequestId_thenItemDto() {

        when(userService.findById(anyLong())).thenReturn(userDto);
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> {
                    Item item = invocationOnMock.getArgument(0, Item.class);
                    item.setId(1L);
                    return item;
                }
        );

        ItemDto testSavedItemDto = itemService.save(itemDto, 1L);

        assertEquals(savedItemDto.toString(), testSavedItemDto.toString());
        verify(userService, only()).findById(1L);
        verify(itemRepository, only()).save(any());
        verify(requestRepository, never()).findById(anyLong());
    }

    @Test
    void save_whenInputValidWithValidRequestId_thenItemDto() {
        itemDto = ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true)
                .requestId(1L)
                .build();
        savedItemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .owner(user)
                .requestId(request.getId())
                .available(true)
                .build();
        when(userService.findById(anyLong())).thenReturn(userDto);
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> {
                    Item item = invocationOnMock.getArgument(0, Item.class);
                    item.setId(1L);
                    return item;
                }
        );

        ItemDto testSavedItemDto = itemService.save(itemDto, 1L);

        assertEquals(savedItemDto.toString(), testSavedItemDto.toString());
        verify(userService, only()).findById(1L);
        verify(itemRepository, only()).save(any());
        verify(requestRepository, only()).findById(anyLong());

    }

    @Test
    void save_whenInputValidWithInvalidRequestId_thenItemDto() {
        itemDto = ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true)
                .requestId(1L)
                .build();
        savedItemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .owner(user)
                .requestId(request.getId())
                .available(true)
                .build();
        when(userService.findById(anyLong())).thenReturn(userDto);
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotExistException.class,
                () -> itemService.save(itemDto, 1L));
        verify(userService, only()).findById(1L);
        verify(itemRepository, never()).save(any());
        verify(requestRepository, only()).findById(anyLong());

    }

    @Test
    void patch_whenInputValid_thenSavedItemDto() {
        ItemDto patchItemDto = ItemDto.builder()
                .name("nameUpdated")
                .description("descUpdated")
                .available(false)
                .requestId(1L)
                .build();
        savedItemDto = ItemDto.builder()
                .id(1L)
                .name("nameUpdated")
                .description("descUpdated")
                .owner(user)
                .available(false)
                .build();
        doNothing().when(userService).checkIfUserExists(anyLong());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(v -> v.getArgument(0, Item.class));

        ItemDto testSavedItemDto = itemService.patch(patchItemDto, user.getId(), item.getId());

        assertEquals(savedItemDto.toString(), testSavedItemDto.toString());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any());

    }

    @Test
    void patch_whenInvalidUserId_thenIncorrectOwnerException() {

        doNothing().when(userService).checkIfUserExists(anyLong());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotExistException.class,
                () -> itemService.patch(any(ItemDto.class), user.getId(), item.getId()));
        verify(itemRepository, only()).findById(anyLong());
    }

    @Test
    void patch_whenUserNotItemOwner_thenIncorrectOwnerException() {
        ItemDto patchItemDto = ItemDto.builder()
                .name("nameUpdated")
                .description("descUpdated")
                .available(false)
                .requestId(1L)
                .build();
        savedItemDto = ItemDto.builder()
                .id(1L)
                .name("nameUpdated")
                .description("descUpdated")
                .owner(user)
                .available(false)
                .build();
        doNothing().when(userService).checkIfUserExists(anyLong());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(IncorrectOwnerException.class,
                () -> itemService.patch(patchItemDto, 300L, item.getId()));
        verify(itemRepository, only()).findById(anyLong());

    }

    @Test
    void patch_whenAllPatchFieldsIsNull_thenIncorrectOwnerException() {
        ItemDto patchItemDto = ItemDto.builder()
                .requestId(1L)
                .build();
        doNothing().when(userService).checkIfUserExists(anyLong());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(v -> v.getArgument(0, Item.class));

        ItemDto testSavedItemDto = itemService.patch(patchItemDto, user.getId(), item.getId());

        assertEquals(savedItemDto.toString(), testSavedItemDto.toString());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any());

    }

    @Test
    void findById_whenInputValid_thenItemDto() {

        savedItemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .owner(user)
                .available(true)
                .comments(Collections.emptyList())
                .lastBooking(ItemDto.BookingDto.builder()
                        .id(lastBooking.getId())
                        .start(lastBooking.getStart())
                        .end(lastBooking.getEnd())
                        .bookerId(lastBooking.getBooker().getId())
                        .build())
                .nextBooking(ItemDto.BookingDto.builder()
                        .id(nextBooking.getId())
                        .start(nextBooking.getStart())
                        .end(nextBooking.getEnd())
                        .bookerId(nextBooking.getBooker().getId())
                        .build())
                .build();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userService.findById(1L)).thenReturn(userDto);
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(Collections.emptyList());
        when(bookingRepository.findFirst1ByItemIdAndStartLessThanAndStatusOrderByStartDesc(
                anyLong(),
                any(LocalDateTime.class),
                any(BookingStatus.class)))
                .thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findFirst1ByItemIdAndStartGreaterThanEqualAndStatusOrderByStartAsc(
                anyLong(),
                any(LocalDateTime.class),
                any(BookingStatus.class)))
                .thenReturn(Optional.of(nextBooking));

        ItemDto testItemDto = itemService.findById(1L, 1L);

        assertEquals(savedItemDto.toString(), testItemDto.toString());
        verify(itemRepository, only()).findById(1L);
        verify(userService, only()).findById(1L);
        verify(commentRepository, only()).findAllByItemId(item.getId());
        verify(bookingRepository, times(1)).findFirst1ByItemIdAndStartLessThanAndStatusOrderByStartDesc(
                anyLong(),
                any(LocalDateTime.class),
                any(BookingStatus.class));
        verify(bookingRepository, times(1)).findFirst1ByItemIdAndStartGreaterThanEqualAndStatusOrderByStartAsc(
                anyLong(),
                any(LocalDateTime.class),
                any(BookingStatus.class));
    }

    @Test
    void findById_whenInvalidItemId_thenItemDto() {

        when((itemRepository.findById(anyLong()))).thenReturn(Optional.empty());

        assertThrows(EntityNotExistException.class,
                () -> itemService.findById(300L, 1L));
        verify(itemRepository, only()).findById(300L);
        verify(userService, never()).findById(1L);
        verify(commentRepository, never()).findAllByItemId(item.getId());
        verify(bookingRepository, never()).findFirst1ByItemIdAndStartLessThanAndStatusOrderByStartDesc(
                anyLong(),
                any(LocalDateTime.class),
                any(BookingStatus.class));
        verify(bookingRepository, never()).findFirst1ByItemIdAndStartGreaterThanEqualAndStatusOrderByStartAsc(
                anyLong(),
                any(LocalDateTime.class),
                any(BookingStatus.class));

    }

    @Test
    void findAllByOwner() {

        itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .available(true)
                .owner(user)
                .lastBooking(ItemDto.BookingDto.builder()
                        .id(lastBooking.getId())
                        .start(lastBooking.getStart())
                        .end(lastBooking.getEnd())
                        .bookerId(lastBooking.getBooker().getId())
                        .build())
                .nextBooking(ItemDto.BookingDto.builder()
                        .id(nextBooking.getId())
                        .start(nextBooking.getStart())
                        .end(nextBooking.getEnd())
                        .bookerId(nextBooking.getBooker().getId())
                        .build())
                .comments(Collections.emptyList())
                .build();
        when(itemRepository.findByOwnerId(eq(user.getId()), any())).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemOwnerIdOrderByStart(user.getId())).thenReturn(List.of(lastBooking, nextBooking));
        when(commentRepository.findAllByItemIn(List.of(item))).thenReturn(Collections.emptyList());

        List<ItemDto> itemDtoList = itemService.findAllByOwner(user.getId(), 0L, 10L);

        assertEquals(List.of(itemDto).size(), itemDtoList.size());
        assertEquals(itemDto.toString(), itemDtoList.get(0).toString());

    }

    @Test
    void search_whenInputValid_thenListOfItem() {
        when(itemRepository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(List.of(item));

        List<ItemDto> itemListDto = itemService.search("text", 0L, 10L);

        assertEquals(List.of(savedItemDto).toString(), itemListDto.toString());
    }

    @Test
    void search_whenInvalidInputText_thenEmptyList() {

        List<ItemDto> itemListDto = itemService.search("", 0L, 10L);

        assertEquals(Collections.emptyList().toString(), itemListDto.toString());
        verify(itemRepository, never()).findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(anyString(), anyString(), any());
    }

    @Test
    void addComment() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .authorName(booker.getName())
                .text("text")
                .build();
        CommentDto inputCommentDto = CommentDto.builder()
                .text("text")
                .build();
        when(bookingRepository.findFirst1ByBookerIdAndItemIdAndEndBeforeAndStatus(anyLong(), anyLong(), any(), any()))
                .thenReturn(Optional.of(lastBooking));
        when(commentRepository.save(any())).thenAnswer(invocationOnMock -> {
                    Comment savedCcomment = invocationOnMock.getArgument(0, Comment.class);
                    savedCcomment.setId(1L);
                    return savedCcomment;
                }
        );

        CommentDto savedCommentDto = itemService.addComment(booker.getId(), item.getId(), inputCommentDto);

        assertEquals(commentDto.getId(), savedCommentDto.getId());
        assertEquals(commentDto.getText(), savedCommentDto.getText());
        assertEquals(commentDto.getAuthorName(), savedCommentDto.getAuthorName());


    }
}