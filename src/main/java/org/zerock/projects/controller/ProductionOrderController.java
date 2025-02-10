package org.zerock.projects.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.projects.domain.ProductionOrder;
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
    ProductionOrderService productionOrderService;
    @Autowired
    ProductionOrderRepository productionOrderRepository;

    @GetMapping("/productionorder")
    public String list(Model model) {
        List<ProductionOrderDTO> productionOrderDTO = productionOrderService.getAllOrders().stream()
                .map(ProductionOrderDTO::fromEntity)
                .collect(Collectors.toList());
        model.addAttribute("orders", productionOrderDTO);
        return "productionorder";
    }

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

    @PostMapping("/addorder")
    public String addOrder(@Valid ProductionOrderDTO productionOrderDTO, BindingResult bindingResult
                            , RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.info("has errors...........");
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/orders/addorder";
        }

        log.info(productionOrderDTO);
        Long id = productionOrderService.addOrder(productionOrderDTO);
        redirectAttributes.addFlashAttribute("result", id);
        return "redirect:/orders/productionorder";
    }

    @PostMapping("/remove")
    public String removeOrder(Long id, RedirectAttributes redirectAttributes) {
        log.info("remove POST.." + id);

        productionOrderService.removeOrder(id);

        redirectAttributes.addFlashAttribute("result", "removed");

        return "redirect:/orders/productionorder";
    }
}
