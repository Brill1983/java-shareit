package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoDated;
import ru.practicum.shareit.service.Create;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemServiceImpl itemService;
    public static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto saveItem(@RequestHeader(HEADER) long userId, @RequestBody @Validated({Create.class}) ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER) long userId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable long itemId) {
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoDated getItemById(@RequestHeader(HEADER) long userId, @PathVariable long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoDated> getUserItems(@RequestHeader(HEADER) long userId) {
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(@RequestHeader(HEADER) long userId,
                                  @PathVariable long itemId,
                                  @RequestBody @Valid CommentDto comment) {
        return itemService.saveComment(userId, itemId, comment);
    }

}
