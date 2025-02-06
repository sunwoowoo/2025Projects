package org.zerock.projects.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.zerock.projects.dto.BoardDTO;
import org.zerock.projects.service.subprocesses.BoardService;

import java.util.List;
//   프레스 , 차제 , 도장 , 조립  의 통합 테이블
@Controller
@Log4j2
@RequiredArgsConstructor
public class BoardController {
    private  final BoardService boardService;
    @GetMapping("/board/list")
    public String getAllBoards(Model model) {
        List<BoardDTO> boards = boardService.findAllBoards();
        log.info("Boards size: {}", boards.size());  // 데이터 개수 확인
        log.info("Boards content: {}", boards);  // 데이터 내용 확인
        model.addAttribute("boards", boards);
        return "board/list";
    }


}
