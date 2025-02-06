package org.zerock.projects.repository;


import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.zerock.projects.domain.subprocesses.Assembly;
import org.zerock.projects.domain.Board;
import org.zerock.projects.domain.subprocesses.Painting;
import org.zerock.projects.repository.subprocesses.AssemblyRepository;
import org.zerock.projects.repository.subprocesses.PaintingRepository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Log4j2
@ExtendWith(SpringExtension.class)
public class AssemblyRepositoryTests {

    @Autowired
    private AssemblyRepository assemblyRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PaintingRepository paintingRepository;
    @Test
    public void BoardTestinsert() {
        IntStream.rangeClosed(1, 10).forEach(i -> {
            // 1. Assembly 저장
            Assembly assembly = Assembly.builder()
                    .AtE(i % 2==0 )
                    .SM(i % 3==0 )
                    .title("조립" + i)
                    .build();
            Assembly savedAssembly = assemblyRepository.save(assembly);
            // 2. Painting 저장
            Painting painting = Painting.builder()
                    .preprocessing(i % 2 == 0)
                    .electrodeposition(i % 3 == 0)
                    .sealing(i % 4 == 0)
                    .MTprocesses(i % 5 == 0)
                    .title("도장" + i)
                    .build();
            Painting savedPainting = paintingRepository.save(painting); //
            // 2. Board 저장 (Assembly 연결 , Painting)
            Board board = Board.builder()
                    .assembly(savedAssembly)
                    .painting(savedPainting)// 엔티티 객체 그대로 사용
                    .progress(i * 10 + "%")
                    .cf("결과 " + i)
                    .build();
            Board savedBoard = boardRepository.save(board); // 철자 수정

            log.info("Inserted Board ID: " + savedBoard.getId() +
                    ", Assembly ID: " + savedAssembly.getAno() +
                    ", Progress: " + board.getProgress());
        });

    }
    @Test
    @DisplayName("Board + Assembly + Painting 조회 테스트")
    public void testFindAllBy() {
        List<Board> boards = boardRepository.findAllBy();

        log.info("===== 전체 Board 데이터 조회 =====");
        boards.forEach(board -> log.info("Board ID: {}, Assembly ID: {}, Painting Color: {}, Progress: {}",
                board.getId(),
                board.getAssembly().getAno(),
                board.getPainting().getPno(),
                board.getProgress()));

        assertFalse(boards.isEmpty(), "데이터가 존재해야 합니다.");
    }
    }




