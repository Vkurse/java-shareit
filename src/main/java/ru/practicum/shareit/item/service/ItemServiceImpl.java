package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.InvalidEntityException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.item.validator.ItemValidator;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemJpaRepository repository;
    private final UserJpaRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemInfoDto> getItems(Long userId) {
        return repository.findAllByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemInfo)
                .peek(this::setBookingToItem)
                .collect(Collectors.toList());
    }

    @Override
    public ItemInfoDto getItem(Long itemId, Long userId) {
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Item not found."));
        ItemInfoDto itemInfoDto = ItemMapper.toItemInfo(item);
        if (item.getOwner().getId().equals(userId)) {
            setBookingToItem(itemInfoDto);
        }
        List<CommentDto> comments = commentRepository.findByItem_Id(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemInfoDto.setComments(comments.isEmpty() ? Collections.emptyList() : comments);

        return itemInfoDto;
    }

    @Override
    public ItemDto addItem(Long userId, ItemDto item) {
        if (ItemValidator.itemCheck(item)) {
            throw new InvalidEntityException("Invalid item body.");
        }
        Item newItem = ItemMapper.toItem(item);
        newItem.setOwner(userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException("Owner not found.")));
        if (item.getRequestId() == null) {
            return ItemMapper.toDto(repository.save(newItem));
        }
        ItemRequest itemRequest = itemRequestRepository.findById(item.getRequestId())
                .orElseThrow(() ->
                        new ObjectNotFoundException("Request not found."));
        newItem.setRequest(itemRequest);
        return ItemMapper.toDto(repository.save(newItem));
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto item) {
        Item updatedItem = repository.findById(itemId)
                .orElseThrow(() ->
                        new ObjectNotFoundException("Item not found."));
        if (!Objects.equals(updatedItem.getOwner().getId(), userId)) {
            throw new ObjectNotFoundException("Item not belongs to this user.");
        }
        Item savedItem = itemUpdate(updatedItem, item);
        repository.save(savedItem);
        return ItemMapper.toDto(savedItem);
    }

    @Transactional
    @Override
    public void deleteItem(Long itemId) {
        if (!repository.existsById(itemId)) {
            throw new ObjectNotFoundException("Item not found.");
        }
        repository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        } else {
            return repository.search(text).stream()
                    .map(ItemMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        validateComment(userId, itemId, commentDto);
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(repository.findById(itemId).orElseThrow(() ->
                new ObjectNotFoundException("Item not found.")));
        comment.setUser(userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException("User not found.")));
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private void setBookingToItem(ItemInfoDto item) {
        List<Booking> bookingList = bookingRepository.findAllByItemIdOrderByStartAsc(item.getId());
        if (!bookingList.isEmpty()) {
            Booking lastBooking = bookingList.stream()
                    .filter(booking -> !booking.getStatus().equals(BookingStatus.REJECTED) &&
                            booking.getStart().isBefore(LocalDateTime.now()))
                    .reduce((booking, booking2) -> booking2)
                    .orElse(null);
            if (lastBooking != null) {
                item.setLastBooking(BookingMapper.toBookingItem(lastBooking));
            }
            Booking nextBooking = bookingList.stream()
                    .filter(booking -> !booking.getStatus().equals(BookingStatus.REJECTED) &&
                            booking.getStart().isAfter(LocalDateTime.now()))
                    .findFirst().orElse(null);
            if (nextBooking != null) {
                item.setNextBooking(BookingMapper.toBookingItem(nextBooking));
            }
        }
    }

    private Item itemUpdate(Item updatedItem, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getOwner() != null) {
            updatedItem.setOwner(userRepository.findById(itemDto.getOwner())
                    .orElseThrow(() -> new ObjectNotFoundException("User not found.")));
        }
        if (itemDto.getRequestId() != null) {
            updatedItem.setRequest(itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new ObjectNotFoundException("Request not found.")));
        }
        return updatedItem;
    }

    private void validateComment(Long userId, Long itemId, CommentDto commentDto) {
        if (commentDto.getText().isEmpty() || commentDto.getText().isBlank()) {
            throw new InvalidEntityException("Invalid comment text.");
        }
        if (!isAlreadyBooked(userId, itemId)) {
            throw new InvalidEntityException("User already hold item.");
        }
        if (isOwner(userId, itemId)) {
            throw new InvalidEntityException("User is owner.");
        }
    }

    private Boolean isAlreadyBooked(Long userId, Long itemId) {
        List<Booking> bookingList = bookingRepository.findByBooker_IdAndItem_IdOrderByStartAsc(userId, itemId);
        if (bookingList.isEmpty()) {
            throw new InvalidEntityException("User is not booked item");
        }
        return bookingList.stream()
                .anyMatch(booking ->
                        booking.getEnd().isBefore(LocalDateTime.now()));
    }

    private Boolean isOwner(Long userId, Long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() ->
                        new ObjectNotFoundException("User not found.")).getOwner().getId().equals(userId);
    }
}