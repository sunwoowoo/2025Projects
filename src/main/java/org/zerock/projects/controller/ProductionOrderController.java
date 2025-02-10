package org.zerock.projects.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.OrderStatus;  // OrderStatus import 추가
import org.zerock.projects.dto.ProductionOrderDTO;
import org.zerock.projects.repository.ProductionOrderRepository;
import org.zerock.projects.service.ProductionOrderService;
import org.zerock.projects.service.machines.ManufacturingSimulator;

import javax.validation.Valid;
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
        return "redirect:/orders/productionorder";
    }

    @PostMapping("/create")
    public String createOrder(@ModelAttribute ProductionOrderDTO orderDTO) {
        log.info("Received new order: {}", orderDTO);

        // orderDTO에서 orderStatus 값이 Enum 값이라면 바로 사용
        OrderStatus orderStatus = orderDTO.getOrderStatus();

        // DTO -> Entity 변환
        ProductionOrder productionOrder = orderDTO.toEntity();
        productionOrder.setOrderStatus(orderStatus);

        // DB에 저장
        ProductionOrder savedOrder = productionOrderRepository.save(productionOrder);

        // 저장된 주문을 DTO로 변환 후 반환
        ProductionOrderDTO savedOrderDTO = ProductionOrderDTO.fromEntity(savedOrder);

        // 성공적으로 저장된 주문 정보 반환
        ResponseEntity.ok(savedOrderDTO);
        return "redirect:/orders/productionorder";
    }

}
