//package org.zerock.projects.repository;
//
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.zerock.projects.domain.Sign.Login;
//import org.zerock.projects.domain.subprocesses.Assembly;
//import org.zerock.projects.domain.Board;
//import org.zerock.projects.domain.subprocesses.Painting;
//import org.zerock.projects.repository.subprocesses.AssemblyRepository;
//import org.zerock.projects.repository.subprocesses.PaintingRepository;
//import org.zerock.projects.service.Sign.SignService;
//
//import javax.persistence.EntityManager;
//import java.util.List;
//import java.util.stream.IntStream;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Log4j2
//public class AssemblyRepositoryTests {
//
//    @Autowired
//    private AssemblyRepository assemblyRepository;
//    @Autowired
//    private BoardRepository boardRepository;
//    @Autowired
//    private EntityManager entityManager;
//    @Autowired
//    private PaintingRepository paintingRepository;
//    @Autowired
//    private SignService signService;  // Spring will inject SignService automatically
//
//    @Test
//    public void BoardTestinsert() {
//        IntStream.rangeClosed(1, 10).forEach(i -> {
//            // 1. Assembly 저장
//            Assembly assembly = Assembly.builder()
//                    .AtE(i % 2 == 0)
//                    .SM(i % 3 == 0)
//                    .title("조립" + i)
//                    .build();
//            Assembly savedAssembly = assemblyRepository.save(assembly);
//
//            // 2. Painting 저장
//            Painting painting = Painting.builder()
//                    .preprocessing(i % 2 == 0)
//                    .electrodeposition(i % 3 == 0)
//                    .sealing(i % 4 == 0)
//                    .MTprocesses(i % 5 == 0)
//                    .title("도장" + i)
//                    .build();
//            Painting savedPainting = paintingRepository.save(painting);
//
//            // 3. Board 저장 (Assembly 연결, Painting)
//            Board board = Board.builder()
//                    .assembly(savedAssembly)
//                    .painting(savedPainting)
//                    .progress("" + i * 10 + "%")
//                    .cf("결과 " + i)
//                    .build();
//            Board savedBoard = boardRepository.save(board);
//
//            log.info("Inserted Board ID: " + savedBoard.getBno() +
//                    ", Assembly ID: " + savedAssembly.getAno() +
//                    ", Painting ID: " + savedPainting.getPno() +
//                    ", Progress: " + savedBoard.getProgress());
//
//            // Assertions to verify the data
//            assertNotNull(savedBoard.getBno(), "Board ID should not be null");
//            assertNotNull(savedAssembly.getAno(), "Assembly ID should not be null");
//            assertNotNull(savedPainting.getPno(), "Painting ID should not be null");
//        });
//    }
//
//    @Test
//    @DisplayName("Board + Assembly + Painting 조회 테스트")
//    public void testFindAllBy() {
//        List<Board> boards = boardRepository.findAllBy();
//
//        log.info("===== 전체 Board 데이터 조회 =====");
//        boards.forEach(board -> log.info("Board ID: {}, Assembly ID: {}, Painting Color: {}, Progress: {}",
//                board.getBno(),
//                board.getAssembly().getAno(),
//                board.getPainting().getPno(),
//                board.getProgress()));
//
//        assertFalse(boards.isEmpty(), "데이터가 존재해야 합니다.");
//    }
//
//    @Test
//    public void testLogin_Success() {
//        // given
//        String userId = "1234";
//        String password = "1234";
//
//        // when
//        Login result = signService.login(userId, password);
//
//        // then
//        assertNotNull(result);
//        assertEquals("1234", result.getUserId());
//        assertEquals("1234", result.getPassword());
//        assertEquals(1, result.getRememberMe()); // 로그인 성공 시 rememberMe는 1
//    }
//
//    @Test
//    public void testLogin_Failure_UserId() {
//        // given
//        String userId = "1111"; // 잘못된 사용자 ID
//        String password = "1234";
//
//        // when
//        Login result = signService.login(userId, password);
//
//        // then
//        assertNull(result);
//        log.info("Test passed: testLogin_Failure_UserId - Login failed due to invalid userId.");
//    }
//}
