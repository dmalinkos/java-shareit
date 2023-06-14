package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.exception.UnavailableError;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingRequestDto bookingRequestDto;
    private BookingDto bookingDto;
    private Item item;
    private User owner;
    private User booker;
    private UserDto bookerDto;
    private final Long itemId = 1L;
    private final Long ownerId = 1L;
    private final Long bookerId = 2L;
    private final Long bookingId = 1L;

    @BeforeEach
    void setup() {

        String ownerName = "ownerName";
        String ownerEmail = "ownerEmail";

        owner = User.builder()
                .id(ownerId)
                .name(ownerName)
                .email(ownerEmail)
                .build();

        String bookerName = "bookerName";
        String bookerEmail = "bookerEmail";

        booker = User.builder()
                .id(bookerId)
                .name(bookerName)
                .email(bookerEmail)
                .build();

        bookerDto = UserDto.builder()
                .id(booker.getId())
                .name(booker.getName())
                .email(booker.getEmail())
                .build();

        String itemName = "itemName";
        String itemDescription = "itemDescription";

        item = Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .owner(owner)
                .available(true)
                .build();

        start = LocalDateTime.now().plusMinutes(1);
        end = start.plusMinutes(1);

        bookingRequestDto = BookingRequestDto.builder()
                .itemId(itemId)
                .start(start)
                .end(end)
                .build();

        bookingDto = BookingDto.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

    }

    @Test
    void save_whenInputValid_thenBookingDto() {

        when(userService.findById(bookerId)).thenReturn(bookerDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenAnswer(
                invocationOnMock -> {
                    Booking booking = invocationOnMock.getArgument(0, Booking.class);
                    booking.setId(1L);
                    return booking;
                }
        );

        BookingDto savedBookingDto = bookingService.save(bookingRequestDto, bookerId);

        verify(userService, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(any());
        assertEquals(bookingDto.toString(), savedBookingDto.toString());

    }

    @Test
    void save_whenBookingRequestDtoItemIdInvalid_thenEntityNotExist() {

        when(userService.findById(bookerId)).thenReturn(bookerDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotExistException.class, () -> bookingService.save(bookingRequestDto, bookerId));
        verify(userService, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(any());
        verify(bookingRepository, never()).save(any());

    }

    @Test
    void save_whenItemUnavailable_thenUnavailableError() {
        item = Item.builder()
                .id(itemId)
                .name("itemName")
                .description("itemDescription")
                .owner(owner)
                .available(false)
                .build();
        when(userService.findById(bookerId)).thenReturn(bookerDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(UnavailableError.class, () -> bookingService.save(bookingRequestDto, bookerId));
        verify(userService, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(any());
        verify(bookingRepository, never()).save(any());

    }

    @Test
    void update_whenInputValidAndApprovedIsTrue_thenBookingDto() {

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(BookingMapper.toBooking(bookingDto)));
        when(bookingRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Booking.class));

        BookingDto updatedBookingDto = bookingService.update(ownerId, bookingId, true);

        assertEquals(updatedBookingDto.getStatus(), BookingStatus.APPROVED);
        assertEquals(bookingDto.getId(), updatedBookingDto.getId());
        assertEquals(bookingDto.getStart(), updatedBookingDto.getStart());
        assertEquals(bookingDto.getEnd(), updatedBookingDto.getEnd());
        assertEquals(bookingDto.getItem(), updatedBookingDto.getItem());
        verify(bookingRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).save(any());

    }

    @Test
    void update_whenInputValidAndApprovedIsFalse_thenBookingDto() {

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(BookingMapper.toBooking(bookingDto)));
        when(bookingRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Booking.class));

        BookingDto updatedBookingDto = bookingService.update(ownerId, bookingId, false);

        assertEquals(updatedBookingDto.getStatus(), BookingStatus.REJECTED);
        assertEquals(bookingDto.getId(), updatedBookingDto.getId());
        assertEquals(bookingDto.getStart(), updatedBookingDto.getStart());
        assertEquals(bookingDto.getEnd(), updatedBookingDto.getEnd());
        assertEquals(bookingDto.getItem(), updatedBookingDto.getItem());
        verify(bookingRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).save(any());

    }

    @Test
    void update_whenInvalidBookingId_thenEntityNotExistException() {

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotExistException.class, () -> bookingService.update(ownerId, bookingId, true));
        verify(bookingRepository, times(1)).findById(any());
        verify(bookingRepository, never()).save(any());

    }

    @Test
    void update_whenUserNotItemOwner_thenEntityNotExistException() {

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(BookingMapper.toBooking(bookingDto)));

        assertThrows(EntityNotExistException.class, () -> bookingService.update(bookerId, bookingId, true));
        verify(bookingRepository, times(1)).findById(any());
        verify(bookingRepository, never()).save(any());

    }

    @Test
    void update_whenBookingAlreadyApprove_thenBadRequestException() {

        bookingDto = BookingDto.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(BookingMapper.toBooking(bookingDto)));

        assertThrows(BadRequestException.class, () -> bookingService.update(ownerId, bookingId, true));
        verify(bookingRepository, times(1)).findById(any());
        verify(bookingRepository, never()).save(any());

    }

    @Test
    void update_whenBookingAlreadyReject_thenBadRequestException() {

        bookingDto = BookingDto.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.REJECTED)
                .build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(BookingMapper.toBooking(bookingDto)));

        assertThrows(BadRequestException.class, () -> bookingService.update(ownerId, bookingId, false));

    }

    @Test
    void findById_whenValidInput_thenBookingDto() {

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(BookingMapper.toBooking(bookingDto)));

        BookingDto foundedBookingDto = bookingService.findById(bookingId, ownerId);

        assertEquals(bookingDto.toString(), foundedBookingDto.toString());
        verify(bookingRepository, times(1)).findById(any());

    }

    @Test
    void findById_whenInvalidBookingId_thenEntityNotExistException() {

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotExistException.class, () -> bookingService.findById(bookingId, ownerId));

    }

    @Test
    void findById_whenInvalidUserId_thenEntityNotExistException() {

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(BookingMapper.toBooking(bookingDto)));

        assertThrows(EntityNotExistException.class, () -> bookingService.findById(bookingId, 300L));

    }

    @Test
    void findAllByState_whenStateIsCurrent_thenInvokeFindAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {

        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(), any(), any()))
                .thenReturn(List.of(BookingMapper.toBooking(bookingDto)));

        List<BookingDto> bookingDtoList = bookingService.findAllByState(BookingState.CURRENT, 1L, 1L, 1L);

        assertEquals(List.of(bookingDto).size(), bookingDtoList.size());
        assertEquals(bookingDto.toString(), bookingDtoList.get(0).toString());
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndEndIsBeforeOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndStartIsAfterOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndStatusOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdOrderByStartDesc(any(), any());

    }

    @Test
    void findAllByState_whenStateIsPast_thenInvokeFindAllByBookerIdAndEndIsBeforeOrderByStartDesc() {

        when(bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(BookingMapper.toBooking(bookingDto)));

        List<BookingDto> bookingDtoList = bookingService.findAllByState(BookingState.PAST, 1L, 1L, 1L);

        assertEquals(List.of(bookingDto).size(), bookingDtoList.size());
        assertEquals(bookingDto.toString(), bookingDtoList.get(0).toString());
        verify(bookingRepository, never()).findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(), any(), any());
        verify(bookingRepository, times(1)).findAllByBookerIdAndEndIsBeforeOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndStartIsAfterOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndStatusOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdOrderByStartDesc(any(), any());

    }

    @Test
    void findAllByState_whenStateIsFuture_thenInvokeFindAllByBookerIdAndEndIsBeforeOrderByStartDesc() {

        when(bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(BookingMapper.toBooking(bookingDto)));

        List<BookingDto> bookingDtoList = bookingService.findAllByState(BookingState.FUTURE, 1L, 1L, 1L);

        assertEquals(List.of(bookingDto).size(), bookingDtoList.size());
        assertEquals(bookingDto.toString(), bookingDtoList.get(0).toString());
        verify(bookingRepository, never()).findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndEndIsBeforeOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, times(1)).findAllByBookerIdAndStartIsAfterOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndStatusOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdOrderByStartDesc(any(), any());

    }

    @Test
    void findAllByState_whenStateIsWaiting_thenInvokeFindAllByBookerIdAndEndIsBeforeOrderByStartDesc() {

        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(BookingMapper.toBooking(bookingDto)));

        List<BookingDto> bookingDtoList = bookingService.findAllByState(BookingState.WAITING, 1L, 1L, 1L);

        assertEquals(List.of(bookingDto).size(), bookingDtoList.size());
        assertEquals(bookingDto.toString(), bookingDtoList.get(0).toString());
        verify(bookingRepository, never()).findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndEndIsBeforeOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndStartIsAfterOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, times(1)).findAllByBookerIdAndStatusOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdOrderByStartDesc(any(), any());

    }

    @Test
    void findAllByState_whenStateIsRejected_thenInvokeFindAllByBookerIdAndEndIsBeforeOrderByStartDesc() {

        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(BookingMapper.toBooking(bookingDto)));

        List<BookingDto> bookingDtoList = bookingService.findAllByState(BookingState.REJECTED, 1L, 1L, 1L);

        assertEquals(List.of(bookingDto).size(), bookingDtoList.size());
        assertEquals(bookingDto.toString(), bookingDtoList.get(0).toString());
        verify(bookingRepository, never()).findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndEndIsBeforeOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndStartIsAfterOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, times(1)).findAllByBookerIdAndStatusOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdOrderByStartDesc(any(), any());

    }

    @Test
    void findAllByState_whenStateIsAll_thenInvokeFindAllByBookerIdAndEndIsBeforeOrderByStartDesc() {

        when(bookingRepository.findAllByBookerIdOrderByStartDesc(any(), any()))
                .thenReturn(List.of(BookingMapper.toBooking(bookingDto)));

        List<BookingDto> bookingDtoList = bookingService.findAllByState(BookingState.ALL, 1L, 1L, 1L);

        assertEquals(List.of(bookingDto).size(), bookingDtoList.size());
        assertEquals(bookingDto.toString(), bookingDtoList.get(0).toString());
        verify(bookingRepository, never()).findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndEndIsBeforeOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndStartIsAfterOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndStatusOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, times(1)).findAllByBookerIdOrderByStartDesc(any(), any());

    }

    @Test
    void findAllByOwner() {
    }

    @Test
    void findAllByOwner_whenStateIsCurrent_thenInvokeFindAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {

        when(bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(), any(), any()))
                .thenReturn(List.of(BookingMapper.toBooking(bookingDto)));

        List<BookingDto> bookingDtoList = bookingService.findAllByOwner(BookingState.CURRENT, 1L, 1L, 1L);

        assertEquals(List.of(bookingDto).size(), bookingDtoList.size());
        assertEquals(bookingDto.toString(), bookingDtoList.get(0).toString());
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStatusOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdOrderByStartDesc(any(), any());

    }

    @Test
    void findAllByOwner_whenStateIsPast_thenInvokeFindAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc() {

        when(bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(BookingMapper.toBooking(bookingDto)));

        List<BookingDto> bookingDtoList = bookingService.findAllByOwner(BookingState.PAST, 1L, 1L, 1L);

        assertEquals(List.of(bookingDto).size(), bookingDtoList.size());
        assertEquals(bookingDto.toString(), bookingDtoList.get(0).toString());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(), any(), any());
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStatusOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdOrderByStartDesc(any(), any());

    }

    @Test
    void findAllByOwner_whenStateIsFuture_thenInvokeFindAllByItemOwnerIdAndStartIsAfterOrderByStartDesc() {

        when(bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(BookingMapper.toBooking(bookingDto)));

        List<BookingDto> bookingDtoList = bookingService.findAllByOwner(BookingState.FUTURE, 1L, 1L, 1L);

        assertEquals(List.of(bookingDto).size(), bookingDtoList.size());
        assertEquals(bookingDto.toString(), bookingDtoList.get(0).toString());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStatusOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdOrderByStartDesc(any(), any());

    }

    @Test
    void findAllByOwner_whenStateIsWaiting_thenInvokeFindAllByItemOwnerIdAndStatusOrderByStartDesc() {

        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(BookingMapper.toBooking(bookingDto)));

        List<BookingDto> bookingDtoList = bookingService.findAllByOwner(BookingState.WAITING, 1L, 1L, 1L);

        assertEquals(List.of(bookingDto).size(), bookingDtoList.size());
        assertEquals(bookingDto.toString(), bookingDtoList.get(0).toString());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStatusOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdOrderByStartDesc(any(), any());

    }

    @Test
    void findAllByOwner_whenStateIsRejected_thenInvokeFindAllByItemOwnerIdAndStatusOrderByStartDesc() {

        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(BookingMapper.toBooking(bookingDto)));

        List<BookingDto> bookingDtoList = bookingService.findAllByOwner(BookingState.REJECTED, 1L, 1L, 1L);

        assertEquals(List.of(bookingDto).size(), bookingDtoList.size());
        assertEquals(bookingDto.toString(), bookingDtoList.get(0).toString());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStatusOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdOrderByStartDesc(any(), any());

    }

    @Test
    void findAllByOwner_whenStateIsAll_thenInvokeFindAllByItemOwnerIdOrderByStartDesc() {

        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(any(), any()))
                .thenReturn(List.of(BookingMapper.toBooking(bookingDto)));

        List<BookingDto> bookingDtoList = bookingService.findAllByOwner(BookingState.ALL, 1L, 1L, 1L);

        assertEquals(List.of(bookingDto).size(), bookingDtoList.size());
        assertEquals(bookingDto.toString(), bookingDtoList.get(0).toString());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStatusOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, times(1)).findAllByItemOwnerIdOrderByStartDesc(any(), any());

    }
}