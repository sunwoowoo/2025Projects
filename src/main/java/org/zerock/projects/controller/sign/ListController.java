package org.zerock.projects.controller.sign;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.zerock.projects.domain.Material;
import org.zerock.projects.service.MaterialService;
import org.zerock.projects.service.ProductionOrderService;
import org.zerock.projects.service.subprocesses.BoardService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

        // 재고 상태에 따른 자재 데이터 (인스톡, 아웃스톡)
        List<Material> inStockMaterials = materialService.getInStockMaterials();
        List<Material> outStockMaterials = materialService.getOutStockMaterials();

        // null 체크 후 size() 호출
        model.addAttribute("inStockCount", (inStockMaterials != null) ? inStockMaterials.size() : 0);
        model.addAttribute("outStockCount", (outStockMaterials != null) ? outStockMaterials.size() : 0);

        // 자재 리스트도 추가 (옵션)
        model.addAttribute("inStockMaterials", inStockMaterials);
        model.addAttribute("outStockMaterials", outStockMaterials);

        return "list";  // list.html 페이지로 반환
    }
}

