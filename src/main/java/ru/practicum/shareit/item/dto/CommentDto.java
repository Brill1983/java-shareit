package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private long id;

    @NotBlank(message = "Передан коммент без текста")
    private String text;

    private ItemDto item;

    private String authorName;

    private LocalDateTime created;
}