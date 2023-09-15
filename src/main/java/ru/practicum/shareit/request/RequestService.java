package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto createItemRequest(long userId, RequestDto requestDto);

    List<RequestDto> getUserItemRequests(long userId);

    Page<RequestDto> getItemRequestsFromOtherUsers(long userId, int from, int size);
}
