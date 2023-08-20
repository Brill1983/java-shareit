package ru.practicum.shareit.item.dao;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImp implements ItemRepository {

    private final Map<Long, Item> itemRepository = new HashMap<>();
    private Long itemIdCounter = 0L;

    @Override
    public Item createItem(Item item) {
        item.setId(++itemIdCounter);
        itemRepository.put(item.getId(), item);
        return itemRepository.get(item.getId());
    }

    @Override
    public Item updateItem(Item item) {
        Item itemFromRep = itemRepository.get(item.getId());
        Optional.ofNullable(item.getName()).ifPresent(itemFromRep::setName);
        Optional.ofNullable(item.getDescription()).ifPresent(itemFromRep::setDescription);
        Optional.ofNullable(item.getAvailable()).ifPresent(itemFromRep::setAvailable);
        return itemRepository.get(item.getId());
    }

    @Override
    public Optional<Item> getItemById(long itemId) {
        return itemRepository.containsKey(itemId) ? Optional.of(itemRepository.get(itemId)) : Optional.empty();
    }

    @Override
    public List<Item> getUserItems(long userId) {
        return itemRepository.values().stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        List<Item> itemList = new ArrayList<>();
        for (Item item : itemRepository.values()) {
            if ((StringUtils.containsIgnoreCase(item.getName(), text) ||
                    StringUtils.containsIgnoreCase(item.getDescription(), text)) &&
                    item.getAvailable()) {
                itemList.add(item);
            }
        }
        return itemList;
    }


}
