package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemDto createItem(long userId, ItemDto itemDto) {
        userRepository.getUserById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);
        Item itemFromRepos = itemRepository.createItem(item);
        return ItemMapper.toItemDto(itemFromRepos);
    }

    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        userRepository.getUserById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
        Item itemFromPer = itemRepository.getItemById(itemId).orElseThrow(() -> new ItemNotFoundException("Предмета с ID " + itemId + " не зарегистрировано"));
        if (itemFromPer.getOwner() != userId) {
            throw new ItemNotFoundException("Пользователь с ID " + userId + " не является владельцем вещи c ID " + itemId + ". Изменение запрещено");
        }
        Item item = ItemMapper.toItem(itemDto);
        item.setId(itemId);
        item.setOwner(userId);

        return ItemMapper.toItemDto(itemRepository.updateItem(item));
    }

    public ItemDto getItemById(long itemId) {
        Item item = itemRepository.getItemById(itemId).orElseThrow(() -> new ItemNotFoundException("Предмета с ID " + itemId + " не зарегистрировано"));

        return ItemMapper.toItemDto(item);
    }

    public List<ItemDto> getUserItems(long userId) {
        userRepository.getUserById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
        List<Item> itemsList = itemRepository.getUserItems(userId);
        return itemsList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

    }

    public List<ItemDto> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> itemsList = itemRepository.search(text);
        return itemsList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
