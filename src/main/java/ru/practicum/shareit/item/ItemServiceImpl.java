package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoDated;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.service.ValidationService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final ValidationService validationService;

    @Transactional
    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        User user = validationService.checkUser(userId);
        Item itemFromDto = ItemMapper.toItem(itemDto, user);
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
        Item item = ItemMapper.toItem(itemDto, itemFromRep);
        item.setId(itemId);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }


    @Transactional(readOnly = true)
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


    @Transactional(readOnly = true)
    @Override
    public ItemDtoDated getItemById(long userId, long itemId) {
        validationService.checkUser(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмета с ID " + itemId + " не зарегистрировано"));
        if (item.getUser().getId() != userId) {
            return ItemMapper.toItemDto(item, null, null);
        }
        List<Booking> lastBookings = bookingRepository.findLastBookingByItemId(itemId, LocalDateTime.now());
        BookingDtoForItem lastBooking = BookingMapper.toItemBookingDto(lastBookings.isEmpty() ? null : lastBookings.get(0));
        List<Booking> nextBookings = bookingRepository.findNearestBookingByItemId(itemId, LocalDateTime.now());
        BookingDtoForItem nextBooking = BookingMapper.toItemBookingDto(nextBookings.isEmpty() ? null : nextBookings.get(0));
        return ItemMapper.toItemDto(item, lastBooking, nextBooking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDtoDated> getUserItems(long userId) {
        validationService.checkUser(userId);
        List<Item> items =  itemRepository.findAllByUserIdOrderById(userId);
        if(items.isEmpty()) {
            throw new ItemNotFoundException("Пользователь " + userId + " не является хозяином ни одной вещи");
        }

        List<ItemDtoDated> datedItemList = new ArrayList<>();
        List<Booking> lastBookings = bookingRepository.findLastBookingsByUserId(userId, LocalDateTime.now());
        List<Booking> nextBookings = bookingRepository.findNextBookingsByUserId(userId, LocalDateTime.now());

        for (Item item : items) {
            Booking lastBooking = null;
            Booking nextBooking = null;
            for (Booking booking : lastBookings) {
                if(booking.getItem().getId().equals(item.getId())) {
                    lastBooking = booking;
                    break;
                }
            }
            for (Booking booking : nextBookings) {
                if(booking.getItem().getId().equals(item.getId())) {
                    nextBooking = booking;
                    break;
                }
            }
            datedItemList.add(ItemMapper.toItemDto(item, BookingMapper.toItemBookingDto(lastBooking), BookingMapper.toItemBookingDto(nextBooking)));
        }
        return datedItemList;
    }
}

