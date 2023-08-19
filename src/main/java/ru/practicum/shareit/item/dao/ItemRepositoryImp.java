package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImp implements ItemRepository {

    private final Map<Long, Item> itemRepository = new HashMap<>();
    Long itemIdCounter = 0L;

    @Override
    public Item createItem(Item item) {
        item.setId(++itemIdCounter);
        itemRepository.put(item.getId(), item);
        return itemRepository.get(item.getId());
    }

    @Override
    public Item updateItem(Item item) {
        Item itemFromRep = itemRepository.get(item.getId());
        if (item.getName() != null) {
            itemFromRep.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemFromRep.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemFromRep.setAvailable(item.getAvailable());
        }
        return itemRepository.get(item.getId());
    }

    @Override
    public Optional<Item> getItemById(long itemId) {
        if (itemRepository.containsKey(itemId)) {
            return Optional.of(itemRepository.get(itemId));
        } else {
            return Optional.empty();
        }
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
            if ((item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                    item.getAvailable()) {
                itemList.add(item);
            }
        }
        return itemList;
    }


}
