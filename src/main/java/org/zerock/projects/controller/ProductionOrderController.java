package org.zerock.projects.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.ProcessType;
import org.zerock.projects.dto.ProductionOrderDTO;
import org.zerock.projects.repository.ProductionOrderRepository;
import org.zerock.projects.service.ProductionOrderService;
import org.zerock.projects.service.machines.ManufacturingSimulator;
import org.zerock.projects.service.search.ProductionOrderSearch;
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
        return "/orders/productionorder";
    }
    // 주문 생성
    @PostMapping("/create")
    public String createOrder(@ModelAttribute ProductionOrderDTO orderDTO) {
        log.info("Received new order: {}", orderDTO);
    }

    @GetMapping("/productionorder/{orderId}")
    public String read(@PathVariable Long orderId, Model model){
        ProductionOrderDTO productionOrderDTO = productionOrderService.readOne(orderId);

        log.info(productionOrderDTO);

        model.addAttribute("poread",productionOrderDTO);
        return "poread";
    }


    @PostMapping("/remove")
    public String removeOrder(Long id, RedirectAttributes redirectAttributes) {
        log.info("Order to delete with ID: {}", id);

        productionOrderService.removeOrder(id);

        redirectAttributes.addFlashAttribute("result", "removed");

        return "redirect:/orders/productionorder";
    }

    @GetMapping("/productionorder/{orderId}")
    public String read(@PathVariable Long orderId, Model model) {
        ProductionOrderDTO productionOrderDTO = productionOrderService.readOne(orderId);

        log.info(productionOrderDTO);

        model.addAttribute("poread", productionOrderDTO);
        return "poread";
    }

    // 수정 페이지로 이동하는 메소드
    @GetMapping("/modify/{orderId}")
    public String modifyOrder(@PathVariable Long orderId, Model model) {
        try {
            ProductionOrder productionOrder = productionOrderService.getOrderById(orderId);
            ProductionOrderDTO productionOrderDTO = ProductionOrderDTO.fromEntity(productionOrder);
            model.addAttribute("poread", productionOrderDTO);
            return "modify";
        } catch (Exception e) {
            log.error("Error occurred while fetching order with ID: {}", orderId, e);
            return "error"; // error.html로 페이지를 리턴하거나 적절한 처리를 합니다.
        }
    }

    @PostMapping("/modify/{orderId}")
    public String updateOrder(@PathVariable Long orderId, @ModelAttribute ProductionOrderDTO orderDTO) {
        try {
            // 모델과 수량만 수정하도록 처리
            productionOrderService.updateOrder(orderId, orderDTO);
            return "redirect:/orders/productionorder/" + orderId;
        } catch (Exception e) {
            log.error("Error while updating order: ", e);
            return "error"; // 예외 발생 시 error 페이지로 리디렉션
        }
    }
}