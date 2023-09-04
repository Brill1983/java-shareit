package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoDated;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
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
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
        Item itemFromDto = ItemMapper.toItem(itemDto, user);
        Item item = itemRepository.save(itemFromDto);
        return ItemMapper.toItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
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
    public ItemDtoDated getItemById(long userId, long itemId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмета с ID " + itemId + " не зарегистрировано"));
        if (item.getUser().getId() != userId) {
            return ItemMapper.toItemDto(item, null, null);
        }
//        Sort sortByStart = Sort.by(Sort.Direction.DESC, "start");
//        Pageable page = PageRequest.of(0,1, sortByStart);
//        Page<Booking> lastBooking = bookingRepository.findLastBookingByUserId(page, userId, itemId);
//        sortByStart = Sort.by(Sort.Direction.ASC, "start");
//        page = PageRequest.of(0,1, sortByStart);
//        Page<Booking> nearestBooking = bookingRepository.findNearestBookingByUserId(page, userId, itemId, LocalDateTime.now());
        List<Booking> lastBookings = bookingRepository.findLastBookingByUserId(userId, itemId, LocalDateTime.now());
        BookingDtoForItem lastBooking = BookingMapper.toItemBookingDto(lastBookings.isEmpty() ? null : lastBookings.get(0));
        List<Booking> nextBookings = bookingRepository.findNearestBookingByUserId(userId, itemId, LocalDateTime.now());
        BookingDtoForItem nextBooking = BookingMapper.toItemBookingDto(nextBookings.isEmpty() ? null : nextBookings.get(0));
        return ItemMapper.toItemDto(item, lastBooking, nextBooking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDtoDated> getUserItems(long userId) {
//        userRepository.findById(userId)
//                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
//        return itemRepository.findAllByUserId(userId).stream()
//                .map(ItemMapper::toItemDto)
//                .collect(Collectors.toList());


        userRepository.findById(userId) //TODO сделать проверку в отдельном классе
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
        List<Item> items =  itemRepository.findAllByUserIdOrderById(userId);
        if(items.isEmpty()) {
            throw new ItemNotFoundException("Пользователь " + userId + " не является хозяином ни одной вещи");
        }
        List<ItemDtoDated> datedItemList = new ArrayList<>();
        for (Item item : items) {
            List<Booking> lastBookings = bookingRepository.findLastBookingByUserId(item.getUser().getId(), item.getId(), LocalDateTime.now());
            List<Booking> nextBookings = bookingRepository.findNearestBookingByUserId(item.getUser().getId(), item.getId(), LocalDateTime.now());
            Booking lastBooking = null;
            Booking nearestBooking = null;
            if(!lastBookings.isEmpty()) {
                lastBooking = lastBookings.get(0);
            }
            if(!nextBookings.isEmpty()) {
                nearestBooking = lastBookings.get(0);
            }
            datedItemList.add(ItemMapper.toItemDto(item, BookingMapper.toItemBookingDto(lastBooking), BookingMapper.toItemBookingDto(nearestBooking)));
        }
        return datedItemList;
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
}
