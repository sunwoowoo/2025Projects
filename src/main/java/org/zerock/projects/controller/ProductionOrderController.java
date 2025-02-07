package org.zerock.projects.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zerock.projects.dto.ProductionOrderDTO;
import org.zerock.projects.service.ProductionOrderService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/orders")
public class ProductionOrderController {
    private final ProductionOrderService productionOrderService;

    @GetMapping("/orderlist")
    public String list(Model model) {
        List<ProductionOrderDTO> productionOrderDTO = productionOrderService.getAllOrders().stream()
                .map(ProductionOrderDTO::fromEntity)
                .collect(Collectors.toList());
        model.addAttribute("orders", productionOrderDTO);
        return "orderlist";
    }
}
