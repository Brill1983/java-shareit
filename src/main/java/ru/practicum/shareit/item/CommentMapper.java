package ru.practicum.shareit.item;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor
public class CommentMapper {

    public static Comment toComment(CommentDto comment, Item item, User user) {
        return new Comment(
                comment.getId(),
                comment.getText(),
                item,
                user,
                LocalDateTime.now()
        );
    }

    public static CommentDto toCommentDto(Comment comment) {

        return new CommentDto(
                comment.getId(),
                comment.getText(),
                ItemMapper.toItemDto(comment.getItem()),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}
