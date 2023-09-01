package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByUserId (Long userId); //TODO

    @Query("select it from Item as it where it.available = true and (upper(it.name) like upper(concat('%', ?1, '%')) or upper(it.description) like upper(concat('%', ?1, '%'))) ")
    List<Item> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String text); // TODO переписать название


//    Item createItem(Item item);
//
//    Item updateItem(Item item);
//
//    Optional<Item> getItemById(long itemId);
//
//    List<Item> getUserItems(long userId);
//
//    List<Item> search(String text);
}
