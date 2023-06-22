package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemMapperTest {
    private Item item;
    private ItemDto itemDto;
    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .name("name")
                .email("email@ya.ru")
                .build();
        itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .owner(user)
                .available(true)
                .build();
        item = Item.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .owner(user)
                .available(true)
                .build();
    }

    @Test
    void toItem() {

        Item mappedItemDto = ItemMapper.toItem(itemDto);

        assertEquals(item, mappedItemDto);

    }

    @Test
    void toItemDto() {

        ItemDto mappedItem = ItemMapper.toItemDto(item);

        assertEquals(itemDto, mappedItem);

    }
}