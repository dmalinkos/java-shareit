package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotBlank;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwner_Id(Long ownerId);

    List<Item> findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(@NotBlank String name, @NotBlank String description);

}
