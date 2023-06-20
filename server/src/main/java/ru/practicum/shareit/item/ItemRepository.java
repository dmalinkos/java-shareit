package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotBlank;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long ownerId, Pageable pageable);

    List<Item> findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(@NotBlank String name, @NotBlank String description, Pageable pageable);

    List<Item> findByRequestId(Long requestId);

    List<Item> findByRequestIdIn(List<Long> requestIds);

}
