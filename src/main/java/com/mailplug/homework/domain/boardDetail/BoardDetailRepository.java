package com.mailplug.homework.domain.boardDetail;

import com.mailplug.homework.aop.timer.Timer;
import jakarta.persistence.LockModeType;
import org.hibernate.sql.Insert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BoardDetailRepository extends JpaRepository<BoardDetail, Long>{
    @Timer
    @Query(value = "select d from BoardDetail d join fetch d.board b where d.board.id = :boardId and d.status = :status order by d.id DESC")
    Page<BoardDetail> findAllByBoardIdByStatusOrderByIdDesc(Long boardId, BoardDetailStatus status, Pageable pageable);
    @Timer
    @Query(value = "select d from BoardDetail d join fetch d.board b where d.board.id = :boardId and d.name like %:name% and d.status = :status order by d.id DESC")
    Page<BoardDetail> findAllByBoardIdNameContainingAndStatusOrderById(Long boardId, String name, BoardDetailStatus status, Pageable pageable);
    @Timer
    @Query(value = "select d from BoardDetail d join fetch d.board b where b.id = :boardId and d.id = :boardDetailId")
    Optional<BoardDetail> findBoardDetail(Long boardId, Long boardDetailId);
}
