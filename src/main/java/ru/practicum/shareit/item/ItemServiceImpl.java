package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.exceptions.BadParameterException;
import ru.practicum.shareit.exceptions.ElementNotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoDated;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.service.ValidationService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final ValidationService validationService;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    @Transactional
    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        User user = validationService.checkUser(userId);
        Request request = null;
        if (itemDto.getRequestId() != null) {
            request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new ElementNotFoundException("Запроса с ID " + itemDto.getRequestId()
                            + " нет в базе"));
        }
        Item itemFromDto = ItemMapper.toItem(itemDto, user, request);
        Item item = itemRepository.save(itemFromDto);
        return ItemMapper.toItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        validationService.checkUser(userId);
        Item itemFromRep = itemRepository.findById(itemId)
                .orElseThrow(() -> new ElementNotFoundException("Предмета с ID " + itemId + " не зарегистрировано"));
        if (itemFromRep.getUser().getId() != userId) {
            throw new ElementNotFoundException("Пользователь с ID " + userId + " не является владельцем вещи c ID "
                    + itemId + ". Изменение запрещено");
        }
        Request request = null;
        if (itemDto.getRequestId() != null) {
            request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new ElementNotFoundException("Запроса с ID " + itemDto.getRequestId()
                            + " нет в базе"));
        }
        Item item = ItemMapper.toItem(itemDto, itemFromRep, request);
        item.setId(itemId);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public List<ItemDto> search(String text, int from, int size) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        Pageable page = PageRequest.of(from / size, size);
        return itemRepository.findByNameOrDescription(text, page).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDtoDated getItemById(long userId, long itemId) {
        validationService.checkUser(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ElementNotFoundException("Предмета с ID " + itemId + " не зарегистрировано"));
        List<CommentDto> comments = commentRepository.findAllByItem_IdOrderByCreatedDesc(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        if (item.getUser().getId() != userId) {
            return ItemMapper.toItemDto(item, null, null, comments);
        }
        Booking lastBookings = bookingRepository.findFirstByItem_IdAndStartBeforeAndStatusOrderByStartDesc(itemId,
                        LocalDateTime.now(), Status.APPROVED)
                .orElse(null);
        Booking nextBookings = bookingRepository.findFirstByItem_IdAndStartAfterAndStatusOrderByStart(itemId,
                        LocalDateTime.now(), Status.APPROVED)
                .orElse(null);
        return ItemMapper.toItemDto(item,
                BookingMapper.toItemBookingDto(lastBookings),
                BookingMapper.toItemBookingDto(nextBookings),
                comments);
    }

    @Override
    public List<ItemDtoDated> getUserItems(long userId, int from, int size) {
        validationService.checkUser(userId);

        Pageable page = PageRequest.of(from / size, size);

        List<Item> items = itemRepository.findAllByUserIdOrderById(userId, page).getContent();
        List<Long> itemsIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<ItemDtoDated> datedItemList = new ArrayList<>();
        List<Booking> lastBookings = bookingRepository.findAllByItem_User_IdAndItem_IdInAndStartBeforeOrderByStartDesc(
                userId, itemsIds, LocalDateTime.now());
        List<Booking> nextBookings = bookingRepository.findAllByItem_User_IdAndItem_IdInAndStartAfterOrderByStart(
                userId, itemsIds, LocalDateTime.now());
        List<Comment> comments = commentRepository.findAllByItem_User_IdOrderByCreatedDesc(userId);

        for (Item item : items) {
            Booking lastBooking = null;
            Booking nextBooking = null;
            List<CommentDto> commentList = new ArrayList<>();
            for (Booking booking : lastBookings) {
                if (booking.getItem().getId().equals(item.getId())) {
                    lastBooking = booking;
                    break;
                }
            }
            for (Booking booking : nextBookings) {
                if (booking.getItem().getId().equals(item.getId())) {
                    nextBooking = booking;
                    break;
                }
            }
            for (Comment comment : comments) {
                if (comment.getItem().getId().equals(item.getId())) {
                    commentList.add(CommentMapper.toCommentDto(comment));
                }
            }
            datedItemList.add(ItemMapper.toItemDto(item,
                    BookingMapper.toItemBookingDto(lastBooking),
                    BookingMapper.toItemBookingDto(nextBooking), commentList));
        }
        return datedItemList;
    }

    @Transactional
    @Override
    public CommentDto saveComment(long userId, long itemId, CommentDto commentDto) {
        User user = validationService.checkUser(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ElementNotFoundException("Предмета с ID " + itemId + " не зарегистрировано"));
        List<Booking> bookings = bookingRepository.findAllByItem_IdAndBooker_IdAndStatusAndStartBeforeAndEndBefore(
                itemId, userId, Status.APPROVED, LocalDateTime.now(), LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new BadParameterException("Пользователь " + userId + " не арендовал вещь " + itemId
                    + ". Не имеет права писать отзыв");
        }
        Comment comment = CommentMapper.toComment(commentDto, item, user);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}

