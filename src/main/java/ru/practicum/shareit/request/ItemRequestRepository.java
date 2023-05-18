package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface ItemRequestRepository extends CrudRepository<ItemRequest, Long> {

    Page<ItemRequest> findAllByRequestorId(Long requestor_id, Pageable pageable);

}
