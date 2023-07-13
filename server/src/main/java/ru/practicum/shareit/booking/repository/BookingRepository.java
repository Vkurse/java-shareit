package ru.practicum.shareit.booking.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndIsBefore(Long userId, LocalDateTime date, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsAfter(Long userId, LocalDateTime date, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime dateTime, LocalDateTime date, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(Long userId, BookingStatus bookingStatus);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndEndIsBefore(Long userId, LocalDateTime date, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStartIsAfter(Long userId, LocalDateTime date, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime dateTime, LocalDateTime date, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStatus(Long userId, BookingStatus bookingStatus);

    List<Booking> findAllByItemIdOrderByStartAsc(Long itemId);

    List<Booking> findByBooker_IdAndItem_IdOrderByStartAsc(Long userId, Long itemId);
}