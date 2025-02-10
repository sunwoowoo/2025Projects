package org.zerock.projects.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.OrderStatus;  // OrderStatus import 추가
import org.zerock.projects.domain.machines.ProcessType;
import org.zerock.projects.dto.ProductionOrderDTO;
import org.zerock.projects.repository.ProductionOrderRepository;
import org.zerock.projects.service.ProductionOrderService;
import org.zerock.projects.service.machines.ManufacturingSimulator;
import org.zerock.projects.service.search.ProductionOrderSearch;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/orders")
public class ProductionOrderController {
    @Autowired
    ProductionOrderSearch productionOrderSearch;
    @Autowired
    private ManufacturingSimulator simulator;
    @Autowired
    private ProductionOrderService productionOrderService;
    @Autowired
    private ProductionOrderRepository productionOrderRepository;

    // 주문 리스트 화면
    @GetMapping("/productionorder")
    public String list(@RequestParam(required = false, defaultValue = "") String types,
                       @RequestParam(required = false, defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "1") int page,
                       Model model) {
        int pageSize = 10; // 한 페이지에 10개씩 표시
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<ProductionOrder> orderPage;
        // 검색 조건 처리
        if (types != null && keyword != null && !keyword.isEmpty()) {
            switch (types) {
                case "carModel":
                    orderPage = productionOrderSearch.searchByKeyword(keyword, pageable);
                    break;
                case "processType":
                    ProcessType processType = ProcessType.valueOf(keyword.toUpperCase());
                    orderPage = productionOrderSearch.searchFromProcessTypes(List.of(processType), pageable);
                    break;
                default:
                    orderPage = productionOrderService.getAllOrders(pageable);
            }
        } else {
            orderPage = productionOrderService.getAllOrders(pageable);
        }

        // DTO 변환 후 모델에 추가
        List<ProductionOrderDTO> orders = orderPage.stream()
                .map(ProductionOrderDTO::fromEntity)
                .collect(Collectors.toList());

        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("types", types);
        model.addAttribute("keyword", keyword);

        return "productionorder";
    }

    // 제작 시뮬레이션
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

    // 주문 생성
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

    @PostMapping("/remove")
    public String removeOrder(Long id, RedirectAttributes redirectAttributes) {
        log.info("Order to delete with ID: {}", id);

        productionOrderService.removeOrder(id);

        redirectAttributes.addFlashAttribute("result", "removed");

        return "redirect:/orders/productionorder";
    }

}
