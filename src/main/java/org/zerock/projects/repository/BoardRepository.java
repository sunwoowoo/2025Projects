package org.zerock.projects.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.zerock.projects.domain.Assembly;
import org.zerock.projects.domain.Board;
import org.zerock.projects.domain.Painting;

import java.util.List;
//   프레스 , 차제 , 도장 , 조립  의 통합 테이블
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 모든 Board + Assembly 정보 조회
    @Query("SELECT b FROM Board b JOIN FETCH b.assembly JOIN FETCH b.painting")
    List<Board> findAllBy();

    List<Board> findAllByAssembly(Assembly assembly);
    List<Board> findAllByPainting(Painting Painting);
}
