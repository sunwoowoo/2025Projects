package org.zerock.projects.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.projects.domain.OrderStatus;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.ProcessType;
import org.zerock.projects.dto.ProductionOrderDTO;
import org.zerock.projects.dto.search.PageRequestDTO;
import org.zerock.projects.repository.ProductionOrderRepository;
import org.zerock.projects.service.ProductionOrderService;
import org.zerock.projects.service.machines.ManufacturingSimulator;
import org.zerock.projects.service.search.ProductionOrderSearch;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Arrays;
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
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate regDate,
                       Model model,
                       PageRequestDTO pageRequestDTO) {
        pageRequestDTO.setType(types);
        pageRequestDTO.setKeyword(keyword);
        pageRequestDTO.setPage(page);
        pageRequestDTO.setRegDate(regDate);
        pageRequestDTO.setSize(10);

        // Use pageRequestDTO's getPageable method instead of creating new PageRequest
        Pageable pageable = pageRequestDTO.getPageable("regDate");  // This will handle the page, size, and sorting
        Page<ProductionOrder> orderPage;

        // 검색 조건 처리
        if (types != null && !types.isEmpty()) {
            switch (types) {
                case "carModel":
                    orderPage = productionOrderSearch.searchByKeyword(keyword, pageable);
                    break;
                case "processType":
                    ProcessType processType = ProcessType.valueOf(keyword.toUpperCase());
                    orderPage = productionOrderSearch.searchFromProcessTypes(List.of(processType), pageable);
                    break;
                case "orderStatus":
                    OrderStatus orderStatus = OrderStatus.valueOf(keyword.toUpperCase());
                    orderPage = productionOrderSearch.searchByStatus(List.of(orderStatus), pageable);
                    break;
                case "regDate":
                    log.info("regDate is : {}", regDate);
                    if (regDate != null) {
                        orderPage = productionOrderSearch.findByRegDate(regDate, pageable);
                    } else {
                        orderPage = productionOrderSearch.findAllOrderByRegDateAsc(pageable);
                    }
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
        model.addAttribute("currentPage", pageRequestDTO.getPage());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        model.addAttribute("regDate", regDate);
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

        // processType이 null일 경우 기본값 설정
        if (order.getProcessType() == null) {
            order.setProcessType(ProcessType.PRESSING); // 기본값을 PRESSING으로 설정
        }

        simulator.simulateProductionOrder(order);
        log.info("Simulation completed");

        return "redirect:/orders/productionorder"; // 리다이렉트 시 경로 수정
    }

    // 주문 생성
    @PostMapping("/create")
    public String createOrder(@Valid ProductionOrder productionOrder, BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size) {
        log.info("productionorder POST create..........");

        if (bindingResult.hasErrors()) {
            log.info("has errors..............");
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/orders/create";
        }

        log.info("ProductionOrder: {}", productionOrder);
        productionOrderService.saveOrder(productionOrder);

        return "redirect:/orders/productionorder?page=" + page;
    }

    @GetMapping("/productionorder/{orderId}")
    public String read(@PathVariable Long orderId, PageRequestDTO pageRequestDTO, Model model){
        ProductionOrderDTO productionOrderDTO = productionOrderService.readOne(orderId);

        log.info(productionOrderDTO);

        model.addAttribute("poread",productionOrderDTO);
        return "poread";
    }

    @PostMapping("/remove")
    public String removeOrder(@RequestParam("orderIds") String orderIdsJson,
                              RedirectAttributes redirectAttributes,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size) {
        try {
            // JSON으로 받은 String orderIDs를 개별의 Long datatype orderIds로 맵핑
            ObjectMapper objectMapper = new ObjectMapper();
            Long[] orderIds = objectMapper.readValue(orderIdsJson, Long[].class);

            log.info("Order to delete with ID: {}", Arrays.toString(orderIds));

            if (orderIds != null && orderIds.length > 0) {
                for (Long id : orderIds) {
                    productionOrderService.removeOrder(id);
                }
                redirectAttributes.addFlashAttribute("result", "removed");
            } else {
                log.warn("No order IDs received for deletion");
                redirectAttributes.addFlashAttribute("result", "noOrdersSelected");
            }
        } catch (Exception e) {
            log.error("Error parsing order IDs: ", e);
            redirectAttributes.addFlashAttribute("result", "error");
        }

        return "redirect:/orders/productionorder?page=" + page + "&size=" + size;
    }

    // 수정 페이지로 이동하는 메소드
    @GetMapping("/modify/{orderId}")
    public String modifyOrder(@PathVariable Long orderId,
                              PageRequestDTO pageRequestDTO, Model model,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size) {

            ProductionOrder productionOrder = productionOrderService.getOrderById(orderId);
            ProductionOrderDTO productionOrderDTO = ProductionOrderDTO.fromEntity(productionOrder);
            model.addAttribute("productionorderread", productionOrderDTO);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            return "productionorder-modify";
    }

    // 수정 페이지 업데이트 method
    @PostMapping("/modify/{orderId}")
    public String updateOrder(@PathVariable Long orderId, @ModelAttribute ProductionOrderDTO orderDTO,
                              PageRequestDTO pageRequestDTO,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size) {
        try {
            // 모델과 수량만 수정하도록 처리
            productionOrderService.updateOrder(orderId, orderDTO);
            return "redirect:/orders/productionorder?page=" + page + "&size=" + size;
        } catch (Exception e) {
            log.error("Error while updating order: ", e);
            return "error"; // 예외 발생 시 error 페이지로 리디렉션
        }
    }

    // 그래프 데이터 method
    @GetMapping("/api/productiongraph")
    @ResponseBody
    public List<ProductionOrderDTO> getGraphData() {
        List<ProductionOrder> orders = productionOrderService.getAllOrdersAsEntity();
        return orders.stream()
                .map(ProductionOrderDTO::fromEntity)
                .collect(Collectors.toList());
    }
}