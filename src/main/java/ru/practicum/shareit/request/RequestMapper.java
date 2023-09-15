package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static Request toItemRequest(RequestDto requestDto, User user) {
        return new Request(
                requestDto.getId(),
                requestDto.getDescription(),
                user,
                requestDto.getCreated() != null ? requestDto.getCreated() : LocalDateTime.now()

        );
    }

    public static RequestDto toItemRequestDto(Request request, List<ItemDto> items) {
        return new RequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                items
        );
    }
}
