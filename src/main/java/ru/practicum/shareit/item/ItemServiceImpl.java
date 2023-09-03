package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
        Item itemFromDto = ItemMapper.toItem(itemDto, user);
        Item item = itemRepository.save(itemFromDto);
        return ItemMapper.toItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
        Item itemFromRep = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмета с ID " + itemId + " не зарегистрировано"));
        if (itemFromRep.getUser().getId() != userId) {
            throw new ItemNotFoundException("Пользователь с ID " + userId + " не является владельцем вещи c ID " + itemId + ". Изменение запрещено");
        }
        Item item = ItemMapper.toItem(itemDto, itemFromRep);
        item.setId(itemId);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getItemById(long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмета с ID " + itemId + " не зарегистрировано"));

        return ItemMapper.toItemDto(item);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getUserItems(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
        return itemRepository.findAllByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> itemsList = itemRepository.findByNameOrDescription(text);
        return itemsList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
