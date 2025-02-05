package org.zerock.projects.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.zerock.projects.dto.BoardDTO;
import org.zerock.projects.service.BoardService;

import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
public class BoardController {
    private  final BoardService boardService;
    @GetMapping("/list")
    public void getAllBoards(Model model) {
        List<BoardDTO> boards = boardService.findAllBoards();
        model.addAttribute("boards", boards);
    }
}
