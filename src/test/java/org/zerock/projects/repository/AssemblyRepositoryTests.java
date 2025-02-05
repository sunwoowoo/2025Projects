package org.zerock.projects.repository;


import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.projects.domain.Assembly;
import org.zerock.projects.domain.Board;

import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class AssemblyRepositoryTests {

    @Autowired
    private AssemblyRepository assemblyRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Test
    public void BoardTestinsert() {
        IntStream.rangeClosed(1, 10).forEach(i -> {
            // 1. Assembly 저장
            Assembly assembly = Assembly.builder()
                    .AtE(i % 2==0 )
                    .SM(i % 3==0 )
                    .build();
            Assembly savedAssembly = assemblyRepository.save(assembly);

            // 2. Board 저장 (Assembly 연결)
            Board board = Board.builder()
                    .assembly(savedAssembly) // 엔티티 객체 그대로 사용
                    .progress(i * 10 + "%")
                    .cf("결과 " + i)
                    .build();
            Board savedBoard = boardRepository.save(board); // 철자 수정

            log.info("Inserted Board ID: " + savedBoard.getId() +
                    ", Assembly ID: " + savedAssembly.getAno());
        });

    }
    @Test
    public void testFindAllBy() {
        // 모든 Board 데이터 조회 (Assembly 포함)
        List<Board> boards = boardRepository.findAllBy();

        log.info("===== 전체 Board 데이터 조회 =====");
        for (Board board : boards) {
            log.info("Board ID: " + board.getId() +
                    ", Assembly ID: " + board.getAssembly().getAno() +
                    ", Progress: " + board.getProgress());
        }
        Assertions.assertFalse(boards.isEmpty(), "데이터가 존재해야 합니다.");
    }

}

