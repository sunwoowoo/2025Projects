package org.zerock.projects.controller.Sign;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.zerock.projects.service.MaterialService;
import org.zerock.projects.service.ProductionOrderService;
import org.zerock.projects.service.subprocesses.BoardService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ListController {

    private final BoardService boardService;
    private final ProductionOrderService productionOrderService;
    private final MaterialService materialService;

    @GetMapping("/list")
    public String listPage(HttpServletRequest request, Model model) {
        // 쿠키에서 userId 가져오기
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
            model.addAttribute("userId", userId);
        } else {
            return "redirect:/login";
        }

        // processType 데이터 개수 그래프
        List<String> processTypes = productionOrderService.getProcessTypes();
        model.addAttribute("processTypes", processTypes);

        // mname 별 재고수량 그래프
        Map<String, Integer> materialData = materialService.getMaterialQuantities();
        model.addAttribute("materialData", materialData);

        return "list";
    }
}
