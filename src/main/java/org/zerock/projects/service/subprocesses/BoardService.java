package org.zerock.projects.service.subprocesses;

import org.zerock.projects.dto.BoardDTO;

import java.util.List;

//   프레스 , 차제 , 도장 , 조립  의 통합 테이블
public interface BoardService {

    Long register(BoardDTO boardDTO);
    List<BoardDTO> findAllBoards(); // 추가: 전체 Board + Assembly 조회
}
