package ru.practicum.shareit.item;

import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {

    List<Comment> findAllByItemId(Long itemID);

    List<Comment> findAllByItemIn(Collection<Item> item);
}
