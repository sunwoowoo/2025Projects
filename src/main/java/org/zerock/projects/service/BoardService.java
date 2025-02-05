package org.zerock.projects.service;

import org.zerock.projects.dto.BoardDTO;

import java.util.List;


public interface BoardService {

    Long register(BoardDTO boardDTO);  // 메서드명은 소문자로 시작하는 것이 일반적
    List<BoardDTO> findAllBoards(); // 추가: 전체 Board + Assembly 조회
}
