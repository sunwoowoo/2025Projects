package org.zerock.projects.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.zerock.projects.domain.Board;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 모든 Board + Assembly 정보 조회
    @Query("SELECT b FROM Board b JOIN FETCH b.assembly")
    List<Board> findAllBy();

}
