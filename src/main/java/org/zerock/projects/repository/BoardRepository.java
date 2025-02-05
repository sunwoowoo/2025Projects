package org.zerock.projects.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.projects.domain.Board;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // Ano 값을 가진 board 조회
    @Query("SELECT b FROM Board b WHERE b.assembly.Ano = :ano")
    List<Board> findByAssemblyAno(@Param("ano") Long ano);
    // 모든 Board + Assembly 정보 조회
    @Query("SELECT b FROM Board b JOIN FETCH b.assembly")
    List<Board> findAllBy();

}
