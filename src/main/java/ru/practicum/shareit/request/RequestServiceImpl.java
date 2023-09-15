package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.service.ValidationService;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final ValidationService validationService;
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public RequestDto createItemRequest(long userId, RequestDto requestDto) {
        User user = validationService.checkUser(userId);
        Request requestFromDto = RequestMapper.toItemRequest(requestDto, user);
        Request request = requestRepository.save(requestFromDto);
        return RequestMapper.toItemRequestDto(request, null);
    }

    @Override
    public List<RequestDto> getUserItemRequests(long userId) {
        validationService.checkUser(userId);
        List<Request> requests = requestRepository.findAllByUser_IdOrderByCreatedDesc(userId);
        List<Long> itemRequestsIds = requests.stream()
                .map(Request::getId)
                .collect(Collectors.toList());

        List<Item> items = itemRepository.findItemsByRequest_IdInOrderById(itemRequestsIds);

        List<RequestDto> requestDtos = new ArrayList<>();
        for (Request request : requests) {
            List<ItemDto> requestItems = new ArrayList<>();
            for (Item item : items) {
                if(item.getRequest().getId() == request.getId()) {
                    requestItems.add(ItemMapper.toItemDto(item));
                }
            }
            requestDtos.add(RequestMapper.toItemRequestDto(request, requestItems));
        }
        return requestDtos;
    }

    @Override
    public Page<RequestDto> getItemRequestsFromOtherUsers(long userId, int from, int size) {
        validationService.checkUser(userId);
        List<Item> items = itemRepository.findItemsByRequestsUserIdNot(userId);

        Pageable page = PageRequest.of(from, size);

        Page<Request> requestList = requestRepository.findAllByUser_IdNot(userId, page);

        return requestList
                .map(request -> {
                    List<ItemDto> itemDtos = new ArrayList<>();
                    for (Item item : items) {
                        if(item.getRequest().getId() == request.getId()) {
                            itemDtos.add(ItemMapper.toItemDto(item));
                        }
                    }
                    return RequestMapper.toItemRequestDto(request, itemDtos);
                });
    }
}
