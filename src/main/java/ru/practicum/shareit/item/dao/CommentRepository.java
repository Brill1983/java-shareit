package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItem_User_IdOrderByCreatedDesc(Long userId);

    List<Comment> findAllByItem_IdOrderByCreatedDesc(Long itemId);
}
