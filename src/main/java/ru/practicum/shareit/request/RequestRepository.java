package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByUser_IdOrderByCreatedDesc(Long userId);

//    @Query("select ir.id from ItemRequest as ir where ir.user.id = ?1 order by ir.created desc ")
//    List<Long> findItemRequestsIdByUserIdOrderByCreatedDesc(Long userId);

    Page<Request> findAllByUser_IdNot(Long userId, Pageable page);

}
