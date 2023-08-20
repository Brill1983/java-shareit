package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private long id;

    @NotBlank(message = "Передан предмет без названия")
    private String name;

    @NotBlank(message = "Передан предмет без описания")
    private String description;

    @NotNull(message = "Передан предмет без указания доступности")
    private Boolean available;
}
