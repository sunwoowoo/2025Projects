package org.zerock.projects.controller.Sign;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.zerock.projects.dto.BoardDTO;
import org.zerock.projects.service.subprocesses.BoardService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ListController {

    private final BoardService boardService;
    @GetMapping("/list")
    public String listPage(HttpServletRequest request, Model model) {
        // 쿠키에서 userId 정보 가져오기
        Cookie[] cookies = request.getCookies();
        String userId = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("userId".equals(cookie.getName())) {
                    userId = cookie.getValue();
                    break;
                }
            }
        }

        if (userId != null) {
            model.addAttribute("userId", userId);  // userId를 list 페이지로 전달
        } else {
            // 쿠키에 정보가 없으면 로그인 페이지로 리디렉션
            return "redirect:/login";
        }

        List<BoardDTO> boards = boardService.findAllBoards();
        model.addAttribute("boards", boards);

        return "list";  // list.html
    }


}
