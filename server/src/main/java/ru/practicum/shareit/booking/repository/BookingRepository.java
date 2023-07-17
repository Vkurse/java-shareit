package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByItemOwner(User user, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User user, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerAndEndBefore(User user, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStartAfter(User user, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStatusEquals(User user, Status status, Pageable pageable);

    Page<Booking> findAllByBooker(User user, Pageable pageable);

    Page<Booking> findAllByBookerAndStartBeforeAndEndAfter(User user, LocalDateTime start, LocalDateTime end,
                                                           Pageable pageable);

    Page<Booking> findAllByBookerAndEndBefore(User user, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByBookerAndStartAfter(User user, LocalDateTime start, Pageable pageable);

    Page<Booking> findAllByBookerAndStatusEquals(User user, Status status, Pageable pageable);

    List<Booking> findAllByItemIdAndItemOwnerIdAndStartBeforeOrderByEndDesc(Long itemId, Long userId, LocalDateTime now);

    List<Booking> findAllByItemIdAndEndBeforeOrderByStartAsc(Long itemId, LocalDateTime now);

    List<Booking> findAllByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long userId, Long itemId, Status status, LocalDateTime end);
}
