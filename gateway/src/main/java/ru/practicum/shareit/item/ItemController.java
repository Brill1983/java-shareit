package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.service.Create;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.service.Constants.HEADER;

@Slf4j
@RestController
@Validated
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestHeader(HEADER) long userId,
                                           @RequestBody @Validated({Create.class}) ItemDto itemDto) {
        log.info("В метод saveItem передан userId {}, itemDto.name: {}, itemDto.description: {}, itemDto.avaliable {}",
                userId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(HEADER) long userId,
                                             @RequestBody ItemDto itemDto,
                                             @PathVariable long itemId) {
        log.info("В метод updateItem передан userId {}, itemId {}, itemDto.name: {}, itemDto.description: {}, " +
                        "itemDto.avaliable {}",
                userId, itemId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
        return itemClient.updateItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(HEADER) long userId,
                                              @PathVariable long itemId) {
        log.info("В метод getItemById передан userId {}, itemId {}", userId, itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(HEADER) long userId,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                               @RequestParam(defaultValue = "20") @Positive int size) {
        log.info("В метод getUserItems передан userId {}, индекс первого элемента {}, количество элементов на " +
                "странице {}", userId, from, size);
        return itemClient.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(HEADER) long userId,
                                         @RequestParam String text) {
        log.info("В метод search передан передан userId {}, text: '{}'", userId, text);
        return itemClient.search(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@RequestHeader(HEADER) long userId,
                                              @PathVariable long itemId,
                                              @RequestBody @Valid CommentDto comment) {
        log.info("В метод saveComment передан userId {}, itemId {}, отзыв с длиной текста: {}",
                userId, itemId, comment.getText().length());
        return itemClient.saveComment(userId, itemId, comment);
    }

}
