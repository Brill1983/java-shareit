package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item createItem(Item item);

    Item updateItem(Item item);

    Optional<Item> getItemById(long itemId);

    List<Item> getUserItems(long userId);

    List<Item> search(String text);
}
