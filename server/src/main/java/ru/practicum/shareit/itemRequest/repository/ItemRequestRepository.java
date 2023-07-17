package ru.practicum.shareit.itemRequest.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.itemRequest.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequesterIdOrderByCreatedAsc(Long userId);

    @Query("select ir from ItemRequest as ir where ir.requester.id not in ?1")
    Page<ItemRequest> findAll(Long userId, Pageable pageable);
}
