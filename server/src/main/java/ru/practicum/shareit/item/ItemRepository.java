package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByOwnerOrderById(Long ownerId, Pageable page);

    List<Item> findByDescriptionContainingIgnoreCase(String description, Pageable page);

    List<Item> findByRequestId(Long requestId);
}
