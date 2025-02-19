package org.zerock.projects.controller.sign;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zerock.projects.domain.Material;
import org.zerock.projects.domain.OrderStatus;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.dto.ProductionOrderDTO;
import org.zerock.projects.service.MaterialService;
import org.zerock.projects.service.ProductionOrderService;
import org.zerock.projects.service.subprocesses.BoardService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

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

        List<OrderStatus> orderStatuses = List.of(OrderStatus.COMPLETED);
        List<ProductionOrder> products = productionOrderService.getCompltedOrdersInList(orderStatuses);
        List<ProductionOrderDTO> productsDTO = products.stream().map(ProductionOrderDTO::fromEntity)
                .collect(Collectors.toList());

        // Create a list or map of quantities
        List<Integer> quantities = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < products.size(); i++) {
            ProductionOrder product = products.get(i);
            quantities.add(product != null ? product.getQuantity() : 0);
            labels.add(product != null ? product.getCarModel() : "Unknown");
        }

        // Add these to your model
        model.addAttribute("productQuantities", quantities);
        model.addAttribute("productLabels", labels);
        model.addAttribute("productsDTO", productsDTO);

        return "list";  // list.html 페이지로 반환
    }

    @GetMapping("/api/products")
    @ResponseBody
    public List<ProductionOrderDTO> getProducts() {
        List<OrderStatus> orderStatuses = List.of(OrderStatus.COMPLETED);
        List<ProductionOrder> products = productionOrderService.getCompltedOrdersInList(orderStatuses);
        return products.stream()
                .map(ProductionOrderDTO::fromEntity)
                .collect(Collectors.toList());
    }
}

