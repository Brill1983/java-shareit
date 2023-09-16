package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.RequestNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.RequestRepository;
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
        List<Item> items = itemRepository.findAllByRequest_IdInOrderById(itemRequestsIds);
        return collectRequestDtoList(requests, items);
    }

    @Override
    public List<RequestDto> getItemRequestsFromOtherUsers(long userId, int from, int size) {
        validationService.checkUser(userId);
        List<Item> items = itemRepository.findByRequest_User_IdNot(userId);
        Pageable page = PageRequest.of(from, size);
        List<Request> requests = requestRepository.findAllByUser_IdNot(userId, page);
        return collectRequestDtoList(requests, items);
    }

    @Override
    public RequestDto getOneItemRequest(long userId, long requestId) {
        validationService.checkUser(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Запроса с ID: " + requestId + " нет в базе"));
        List<ItemDto> itemDtos = itemRepository.findAllByRequest_IdInOrderById(List.of(requestId)).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return RequestMapper.toItemRequestDto(request, itemDtos);
    }

    private List<RequestDto> collectRequestDtoList(List<Request> requests, List<Item> items) {
        List<RequestDto> requestDtos = new ArrayList<>();
        for (Request request : requests) {
            List<ItemDto> requestItems = new ArrayList<>();
            for (Item item : items) {
                if (item.getRequest().getId() == request.getId()) {
                    requestItems.add(ItemMapper.toItemDto(item));
                }
            }
            requestDtos.add(RequestMapper.toItemRequestDto(request, requestItems));
        }
        return requestDtos;
    }
}
