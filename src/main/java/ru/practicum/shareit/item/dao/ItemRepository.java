package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByUserIdOrderById(Long userId);

    @Query("select it " +
            "from Item as it " +
            "where it.available = true and (upper(it.name) " +
            "like upper(concat('%', ?1, '%')) or upper(it.description) like upper(concat('%', ?1, '%'))) ")
    List<Item> findByNameOrDescription(String text);

//    List<Item> findAllByItemRequest_IdOrderById(Long requestId);

    @Query("select it from Item as it where it.request.id in ?1 order by it.id")
    List<Item> findItemsByRequest_IdInOrderById(List<Long> itemRequestIds);

    @Query("select it from Item as it where it.request.user.id <> ?1 order by it.id")
    List<Item> findItemsByRequestsUserIdNot(Long userId);
}
