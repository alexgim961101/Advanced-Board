package com.mailplug.homework.domain.boardDetail;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardDetailRepository extends JpaRepository<BoardDetail, Long> {
    Page<BoardDetail> findAllByOrderByIdDesc(Pageable pageable);
    Page<BoardDetail> findAllByNameContainingOrderById(String name, Pageable pageable);
}
