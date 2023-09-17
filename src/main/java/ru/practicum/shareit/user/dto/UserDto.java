package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.service.Create;
import ru.practicum.shareit.service.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private long id;

    private String name;

    @NotBlank(groups = {Create.class}, message = "Передан пустой email")
    @Email(groups = {Create.class, Update.class}, message = "Передан неправильный формат email")
    private String email;
}
