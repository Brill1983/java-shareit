package ru.practicum.shareit.item;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoDated;

import java.util.List;

@Transactional(readOnly = true)
public interface ItemService {

    @Transactional
    ItemDto createItem(long userId, ItemDto itemDto);

    @Transactional
    ItemDto updateItem(long userId, ItemDto itemDto, long itemId);

    @Transactional(readOnly = true)
    ItemDtoDated getItemById(long userId, long itemId);

    @Transactional(readOnly = true)
    List<ItemDtoDated> getUserItems(long userId);

    @Transactional(readOnly = true)
    List<ItemDto> search(String text);

}
