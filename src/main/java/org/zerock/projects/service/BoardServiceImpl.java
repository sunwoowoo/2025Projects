package org.zerock.projects.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.zerock.projects.domain.Board;
import org.zerock.projects.dto.BoardDTO;
import org.zerock.projects.repository.BoardRepository;

import java.util.List;
import java.util.stream.Collectors;

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
        return savedBoard.getId();
    }

    @Override
    public List<BoardDTO> findByAssemblyAno(Long ano) {
        List<Board> boards = boardRepository.findByAssemblyAno(ano);
        return boards.stream()
                .map(board -> modelMapper.map(board, BoardDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<BoardDTO> findAllBoards() {
        List<Board> boards = boardRepository.findAllBy();
        return boards.stream()
                .map(board -> modelMapper.map(board, BoardDTO.class))
                .collect(Collectors.toList());
    }

}
