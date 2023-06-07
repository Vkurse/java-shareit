package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemJpaRepository extends JpaRepository<Item, Long> {
    @Query("SELECT it FROM Item it " +
            "WHERE (LOWER(it.name) LIKE LOWER(CONCAT('%', :text, '%') ) OR " +
            "LOWER(it.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND it.available = true")
    List<Item> search(@Param("text") String text);

    List<Item> findAllByOwnerId(Long ownerId);
}