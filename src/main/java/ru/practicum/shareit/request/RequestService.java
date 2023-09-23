package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto createItemRequest(long userId, RequestDto requestDto);

    List<RequestDto> getUserItemRequests(long userId);

    List<RequestDto> getItemRequestsFromOtherUsers(long userId, int from, int size);

    RequestDto getOneItemRequest(long userId, long requestId);
}
