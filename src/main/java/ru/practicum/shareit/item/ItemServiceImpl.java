package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.exceptions.BadParameterException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.RequestNotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoDated;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
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
        if(itemDto.getRequestId() != null) {
            request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new RequestNotFoundException("Запроса с ID " + itemDto.getRequestId() + " нет в базе"));
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
                .orElseThrow(() -> new ItemNotFoundException("Предмета с ID " + itemId + " не зарегистрировано"));
        if (itemFromRep.getUser().getId() != userId) {
            throw new ItemNotFoundException("Пользователь с ID " + userId + " не является владельцем вещи c ID " + itemId + ". Изменение запрещено");
        }
        Request request = null;
        if(itemDto.getRequestId() != null) {
            request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new RequestNotFoundException("Запроса с ID " + itemDto.getRequestId() + " нет в базе"));
        }
        Item item = ItemMapper.toItem(itemDto, itemFromRep, request);
        item.setId(itemId);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> itemsList = itemRepository.findByNameOrDescription(text);
        return itemsList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDtoDated getItemById(long userId, long itemId) {
        validationService.checkUser(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмета с ID " + itemId + " не зарегистрировано"));
        List<CommentDto> comments = commentRepository.findCommentsByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        if (item.getUser().getId() != userId) {
            return ItemMapper.toItemDto(item, null, null, comments);
        }
        List<Booking> lastBookings = bookingRepository.findLastBookingByItemId(itemId, LocalDateTime.now());
        BookingDtoForItem lastBooking = BookingMapper.toItemBookingDto(lastBookings.isEmpty() ? null : lastBookings.get(0));
        List<Booking> nextBookings = bookingRepository.findNextBookingByItemId(itemId, LocalDateTime.now());
        BookingDtoForItem nextBooking = BookingMapper.toItemBookingDto(nextBookings.isEmpty() ? null : nextBookings.get(0));

        return ItemMapper.toItemDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemDtoDated> getUserItems(long userId) {
        validationService.checkUser(userId);
        List<Item> items = itemRepository.findAllByUserIdOrderById(userId);
        if (items.isEmpty()) {
            throw new ItemNotFoundException("Пользователь " + userId + " не является хозяином ни одной вещи");
        }

        List<ItemDtoDated> datedItemList = new ArrayList<>();
        List<Booking> lastBookings = bookingRepository.findLastBookingsByUserId(userId, LocalDateTime.now());
        List<Booking> nextBookings = bookingRepository.findNextBookingsByUserId(userId, LocalDateTime.now());
        List<Comment> comments = commentRepository.findCommentsForItemsByOwnerId(userId);

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
            datedItemList.add(ItemMapper.toItemDto(item, BookingMapper.toItemBookingDto(lastBooking), BookingMapper.toItemBookingDto(nextBooking), commentList));
        }
        return datedItemList;
    }

    @Transactional
    @Override
    public CommentDto saveComment(long userId, long itemId, CommentDto commentDto) {
        User user = validationService.checkUser(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмета с ID " + itemId + " не зарегистрировано"));
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndItemId(userId, itemId, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new BadParameterException("Пользователь " + userId + " не арендовал вещь " + itemId + ". Не имеет права писать отзыв");
        }
        Comment comment = CommentMapper.toComment(commentDto, item, user);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}

