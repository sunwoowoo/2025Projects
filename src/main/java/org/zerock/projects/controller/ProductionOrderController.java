package org.zerock.projects.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
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
    ProductionOrderService productionOrderService;
    @Autowired
    ProductionOrderRepository productionOrderRepository;

    @GetMapping("/orderlist")
    public String list(Model model) {
        List<ProductionOrderDTO> productionOrderDTO = productionOrderService.getAllOrders().stream()
                .map(ProductionOrderDTO::fromEntity)
                .collect(Collectors.toList());
        model.addAttribute("orders", productionOrderDTO);
        return "orderlist";
    }

    @GetMapping("/simulate/{orderId}")
    public String simulateOrder(@PathVariable Long orderId) {
        log.info("Simulating order with ID: {}", orderId);
        ProductionOrder order = productionOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        log.info("Order found: {} : {}", orderId, order);
        simulator.simulateProductionOrder(order);
        log.info("Simulation completed");
        return "redirect:/orders/orderlist";
    }

    @GetMapping(value = "/order-progress/{orderId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamOrderProgress(@PathVariable Long orderId) {
        SseEmitter emitter = new SseEmitter();
        // Store emitter somewhere to be used by the simulator
        return emitter;
    }
}
