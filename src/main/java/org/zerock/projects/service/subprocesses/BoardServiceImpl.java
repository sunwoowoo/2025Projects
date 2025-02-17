package org.zerock.projects.service.subprocesses;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.zerock.projects.domain.Board;
import org.zerock.projects.dto.BoardDTO;
import org.zerock.projects.repository.BoardRepository;

import java.util.List;
import java.util.stream.Collectors;
//   프레스 , 차제 , 도장 , 조립  의 통합 테이블
@Service
@Log4j2
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final ModelMapper modelMapper;
    private final BoardRepository boardRepository;
    @Override
    public Long register(BoardDTO boardDTO) {
        Board board = modelMapper.map(boardDTO, Board.class);
        Board savedBoard = boardRepository.save(board); // JPA 저장
        return savedBoard.getBno();
    }

    @Override
    public List<BoardDTO> findAllBoards() {
        List<Board> boards = boardRepository.findAllBy();
        return boards.stream()
                .map(board -> modelMapper.map(board, BoardDTO.class))
                .collect(Collectors.toList());
    }

}
