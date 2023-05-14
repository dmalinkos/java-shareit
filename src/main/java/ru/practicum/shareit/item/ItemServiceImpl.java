package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.exception.IncorrectOwnerException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private static final String ITEM_NOT_EXIST_MSG = "Item with 'id = %d' is not exist";
    private static final String INCORRECT_ITEM_OWNER = "User with 'id = %d is not owner of item with 'id = %d'";
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto add(ItemDto itemDto, Long userId) {
        User owner = UserMapper.toUser(userService.findById(userId));
        Item addingItem = ItemMapper.toItem(itemDto);
        addingItem.setOwner(owner);
        Item item = itemRepository.save(addingItem);
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto patch(ItemDto patchItemDto, Long userId, Long itemId) {
        userService.checkIfUserExists(userId);
        Item curItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotExistException(String.format(ITEM_NOT_EXIST_MSG, itemId)));
        if (!Objects.equals(curItem.getOwner().getId(), userId)) {
            throw new IncorrectOwnerException(String.format(INCORRECT_ITEM_OWNER, userId, itemId));
        }
        if (patchItemDto.getName() != null) {
            curItem.setName(patchItemDto.getName());
        }
        if (patchItemDto.getDescription() != null) {
            curItem.setDescription(patchItemDto.getDescription());
        }
        if (patchItemDto.getAvailable() != null) {
            curItem.setAvailable(patchItemDto.getAvailable());
        }
        Item patchedItem = itemRepository.save(curItem);
        return ItemMapper.toItemDto(patchedItem);
    }

    @Override
    @Transactional
    public ItemDto findById(Long itemId, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotExistException(String.format(ITEM_NOT_EXIST_MSG, itemId)));
        User owner = UserMapper.toUser(userService.findById(userId));
        Optional<Booking> lastBooking = Optional.empty();
        Optional<Booking> nextBooking = Optional.empty();
        List<CommentDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        if (Objects.equals(item.getOwner().getId(), userId)) {
            lastBooking = bookingRepository.findFirst1ByItemIdAndStartLessThanAndStatusOrderByStartDesc(itemId, now, BookingStatus.APPROVED);
            nextBooking = bookingRepository.findFirst1ByItemIdAndStartGreaterThanEqualAndStatusOrderByStartAsc(itemId, now, BookingStatus.APPROVED);
        }
        return ItemDto.builder()
                .id(item.getId())
                .owner(owner)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking.map(booking -> ItemDto.BookingDto.builder()
                        .id(booking.getId())
                        .start(booking.getStart())
                        .end(booking.getEnd())
                        .bookerId(booking.getBooker().getId())
                        .build()).orElse(null))
                .nextBooking(nextBooking.map(booking -> ItemDto.BookingDto.builder()
                        .id(booking.getId())
                        .start(booking.getStart())
                        .end(booking.getEnd())
                        .bookerId(booking.getBooker().getId())
                        .build()).orElse(null))
                .comments(comments)
                .build();
    }

    @Override
    @Transactional
    public List<ItemDto> findAllByOwner(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        List<Item> items = itemRepository.findByOwner_Id(userId);
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdOrderByStart(userId);
        Map<Item, List<Booking>> bookingGroupByItem = bookings.stream()
                .collect(Collectors.groupingBy(Booking::getItem));
        Map<Item, List<Comment>> commentsGroupByItem = commentRepository.findAllByItemIn(items).stream()
                .collect(Collectors.groupingBy(Comment::getItem));
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            Optional<Booking> lastBooking;
            Optional<Booking> nextBooking;
            if (bookingGroupByItem.get(item) != null) {
                lastBooking = bookingGroupByItem.get(item).stream()
                        .filter(book -> !book.getStart().isAfter(now) && book.getEnd().isBefore(now))
                        .findFirst();
                nextBooking = bookingGroupByItem.get(item).stream()
                        .filter(book -> book.getStart().isAfter(now))
                        .findFirst();
            } else {
                lastBooking = Optional.empty();
                nextBooking = Optional.empty();
            }
            List<Comment> comments = commentsGroupByItem.get(item);
            List<CommentDto> commentDtos;
            if (comments == null) {
                commentDtos = Collections.emptyList();
            } else {
                commentDtos = comments.stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList());
            }

            ItemDto itemDto = ItemDto.builder()
                    .id(item.getId())
                    .owner(item.getOwner())
                    .name(item.getName())
                    .description(item.getDescription())
                    .available(item.getAvailable())
                    .lastBooking(lastBooking.map(booking -> ItemDto.BookingDto.builder()
                            .id(booking.getId())
                            .start(booking.getStart())
                            .end(booking.getEnd())
                            .bookerId(booking.getBooker().getId())
                            .build()).orElse(null))
                    .nextBooking(nextBooking.map(booking -> ItemDto.BookingDto.builder()
                            .id(booking.getId())
                            .start(booking.getStart())
                            .end(booking.getEnd())
                            .bookerId(booking.getBooker().getId())
                            .build()).orElse(null))
                    .comments(commentDtos)
                    .build();
            itemDtos.add(itemDto);
        }
        return itemDtos.stream().sorted(Comparator.comparingLong(ItemDto::getId)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = bookingRepository.findFirst1ByBookerIdAndItemIdAndEndBeforeAndStatus(userId, itemId, now, BookingStatus.APPROVED)
                .orElseThrow(() -> new BadRequestException(String.format("User id=%d did not book Item=%d", userId, itemId)));
        Item item = booking.getItem();
        User user = booking.getBooker();
        Comment comment = Comment.builder()
                .author(user)
                .item(item)
                .text(commentDto.getText())
                .build();
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}
