package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findAllByUserIdOrderById(Long userId, Pageable page);

    @Query("select it " +
            "from Item as it " +
            "where it.available = true and (upper(it.name) " +
            "like upper(concat('%', ?1, '%')) or upper(it.description) like upper(concat('%', ?1, '%'))) ")
    Page<Item> findByNameOrDescription(String text, Pageable page);

    List<Item> findAllByRequest_IdInOrderById(List<Long> itemRequestIds);

    List<Item> findByRequest_User_IdNot(Long userId);
}
