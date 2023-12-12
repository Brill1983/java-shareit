package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

import static ru.practicum.shareit.service.Constants.HEADER;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> saveItemRequest(@RequestHeader(HEADER) long userId, @RequestBody @Valid RequestDto requestDto) {
        log.info("В метод saveItemRequest передан userId {}, itemRequestDto.description: {}",
                userId, requestDto.getDescription());
        return requestClient.createItemRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequests(@RequestHeader(HEADER) long userId) { // список СВОИХ запросов
        log.info("В метод getItemRequests передан userId {}", userId);
        return requestClient.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestsFromOtherUsers(@RequestHeader(HEADER) long userId,
                                                                @RequestParam(defaultValue = "0") @Min(0) int from,
                                                                @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("В метод getItemRequestsFromOtherUsers передан userId {}, индекс первого элемента {}, " +
                "количество элементов на странице {}", userId, from, size);
        return requestClient.getItemRequestsFromOtherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getOneItemRequest(@RequestHeader(HEADER) long userId, @PathVariable long requestId) {
        log.info("В метод getOneItemRequest передан userId: {}, requestId: {}", userId, requestId);
        return requestClient.getOneItemRequest(userId, requestId);
    }
}
