package org.zerock.projects.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.projects.domain.OrderStatus;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.dto.ProductionOrderDTO;
import org.zerock.projects.dto.search.PageRequestDTO;
import org.zerock.projects.service.ProductionOrderService;
import org.zerock.projects.service.search.ProductionOrderSearch;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/completed-orders")
public class CompleteController {
    @Autowired
    ProductionOrderSearch productionOrderSearch;
    @Autowired
    ProductionOrderService productionOrderService;

    // 완성품 재고 페이지 리스트
    @GetMapping("/completed")
    public String completeList(@RequestParam(required = false, defaultValue = "") String types,
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
        Page<ProductionOrder> orderPage = null;
        List<OrderStatus> completed = List.of(OrderStatus.COMPLETED);

        // 완성된 주문들만 가져오기
        orderPage = productionOrderService.getCompletedOrders(pageable, completed);
        List<OrderStatus> completedStatus = List.of(OrderStatus.COMPLETED);

        // 검색 조건 처리
        if (types != null && !types.isEmpty()) {
            switch (types) {
                case "carModel":
                    orderPage = productionOrderSearch.findByKeywordAndStatus(keyword, completedStatus, pageable);
                    break;
                case "regDate":
                    log.info("regDate is : {}", regDate);
                    if (regDate != null) {
                        orderPage = productionOrderSearch.findByRegDateAndStatus(regDate, completedStatus, pageable);
                    } else {
                        orderPage = productionOrderService.getCompletedOrders(pageable, completedStatus);
                    }
                    break;
                default:
                    orderPage = productionOrderService.getCompletedOrders(pageable, completedStatus);
            }
        } else {
            orderPage = productionOrderService.getCompletedOrders(pageable, completedStatus);
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

        return "completed";
    }

    @GetMapping("/completed/{orderId}")
    public String completeRead(@PathVariable Long orderId, PageRequestDTO pageRequestDTO, Model model) {
        ProductionOrderDTO productionOrderDTO = productionOrderService.readOne(orderId);

        log.info(productionOrderDTO);

        model.addAttribute("completeRead", productionOrderDTO);
        return "completed-read";
    }

    // 수정 페이지로 이동하는 메소드
    @GetMapping("/modify/{orderId}")
    public String completeModifyOrder(@PathVariable Long orderId,
                                      PageRequestDTO pageRequestDTO, Model model,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size) {

        ProductionOrder productionOrder = productionOrderService.getOrderById(orderId);
        ProductionOrderDTO productionOrderDTO = ProductionOrderDTO.fromEntity(productionOrder);
        model.addAttribute("completeRead", productionOrderDTO);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        return "completed-modify";
    }

    // 수정 페이지 업데이트 method
    @PostMapping("/modify/{orderId}")
    public String completeUpdateOrder(@PathVariable Long orderId, @ModelAttribute ProductionOrderDTO orderDTO,
                                      PageRequestDTO pageRequestDTO,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        try {
            // 모델과 수량만 수정하도록 처리
            productionOrderService.updateOrder(orderId, orderDTO);
            return "redirect:/completed-orders/completed?page=" + page + "&size=" + size ;
        } catch (Exception e) {
            log.error("Error while updating order: ", e);
            return "error"; // 예외 발생 시 error 페이지로 리디렉션
        }
    }

    @PostMapping("/remove")
    public String removeOrder(@RequestParam("orderIds") String orderIdsJson, RedirectAttributes redirectAttributes) {
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

        return "redirect:/completed-orders/completed";
    }

    // 그래프 데이터 method
    @GetMapping("/api/completedgraph")
    @ResponseBody
    public List<ProductionOrderDTO> getGraphData(Pageable pageable) {
        List<OrderStatus> orderStatuses = List.of(OrderStatus.COMPLETED);
        List<ProductionOrder> orders = productionOrderService.getCompltedOrdersInList(orderStatuses);
        return orders.stream()
                .map(ProductionOrderDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
