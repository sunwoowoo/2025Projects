package org.zerock.projects.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.dto.ProductionOrderDTO;
import org.zerock.projects.repository.ProductionOrderRepository;
import org.zerock.projects.service.ProductionOrderService;
import org.zerock.projects.service.machines.ManufacturingSimulator;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/orders")
public class ProductionOrderController {

    @Autowired
    private ManufacturingSimulator simulator;
    @Autowired
    private ProductionOrderService productionOrderService;
    @Autowired
    private ProductionOrderRepository productionOrderRepository;

    // 주문 리스트 화면
    @GetMapping("/productionorder")
    public String list(Model model) {
        List<ProductionOrderDTO> productionOrderDTO = productionOrderService.getAllOrders().stream()
                .map(ProductionOrderDTO::fromEntity)
                .collect(Collectors.toList());
        model.addAttribute("orders", productionOrderDTO);
        return "productionorder";
    }

    // 주문 시뮬레이션
    @GetMapping("/simulate/{orderId}")
    public String simulateOrder(@PathVariable Long orderId) {
        log.info("Simulating order with ID: {}", orderId);
        ProductionOrder order = productionOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        log.info("Order found: {} : {}", orderId, order);
        simulator.simulateProductionOrder(order);
        log.info("Simulation completed");
        return "/orders/productionorder";
    }

    @GetMapping("/productionorder/{orderId}")
    public String read(@PathVariable Long orderId, Model model){
        ProductionOrderDTO productionOrderDTO = productionOrderService.readOne(orderId);

        log.info(productionOrderDTO);

        model.addAttribute("poread",productionOrderDTO);
        return "poread";
    }
}
