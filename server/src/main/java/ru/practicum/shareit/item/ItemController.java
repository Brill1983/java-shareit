package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoDated;

import java.util.List;

import static ru.practicum.shareit.booking.Constants.HEADER;

@Slf4j
@RestController
@Validated
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto saveItem(@RequestHeader(HEADER) long userId,
                            @RequestBody ItemDto itemDto) {
        log.info("В метод saveItem передан userId {}, itemDto.name: {}, itemDto.description: {}, itemDto.avaliable {}",
                userId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER) long userId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable long itemId) {
        log.info("В метод updateItem передан userId {}, itemId {}, itemDto.name: {}, itemDto.description: {}, " +
                        "itemDto.avaliable {}",
                userId, itemId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoDated getItemById(@RequestHeader(HEADER) long userId, @PathVariable long itemId) {
        log.info("В метод getItemById передан userId {}, itemId {}", userId, itemId);
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoDated> getUserItems(@RequestHeader(HEADER) long userId,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "20") int size) {
        log.info("В метод getUserItems передан userId {}, индекс первого элемента {}, количество элементов на " +
                "странице {}", userId, from, size);
        return itemService.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @RequestParam(defaultValue = "0") int from,
                                @RequestParam(defaultValue = "20") int size) {
        log.info("В метод search передан text: '{}', индекс первого элемента {}, количество элементов на " +
                "странице {}", text, from, size);
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(@RequestHeader(HEADER) long userId,
                                  @PathVariable long itemId,
                                  @RequestBody CommentDto comment) {
        log.info("В метод saveComment передан userId {}, itemId {}, отзыв с длиной текста: {}",
                userId, itemId, comment.getText().length());
        return itemService.saveComment(userId, itemId, comment);
    }
}
