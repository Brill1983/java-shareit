package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.service.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ItemDto {

    private long id;

    @NotBlank(groups = {Create.class}, message = "Передан предмет без названия")
    private String name;

    @NotBlank(groups = {Create.class}, message = "Передан предмет без описания")
    private String description;

    @NotNull(groups = {Create.class}, message = "Передан предмет без указания доступности")
    private Boolean available;

    private Long requestId;
}
