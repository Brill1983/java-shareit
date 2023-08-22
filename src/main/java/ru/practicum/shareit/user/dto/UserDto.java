package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private long id;

    private String name;

    @NotBlank(groups = {Create.class}, message = "Передан пустой email")
    @Email(groups = {Create.class}, message = "Передан неправильный формат email")
    private String email;
}
