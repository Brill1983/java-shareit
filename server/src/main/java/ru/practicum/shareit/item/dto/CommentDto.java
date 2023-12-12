package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private long id;

    private String text;

    private ItemDto item;

    private String authorName;

    private LocalDateTime created;
}