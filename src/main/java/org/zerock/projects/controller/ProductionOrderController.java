package org.zerock.projects.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.service.ProductionOrderService;

import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
public class ProductionOrderController {
    @Autowired
    private ProductionOrderService service;

    @GetMapping("/productionorder")
    public void getAllOrders(Model model) {
        model.addAttribute("orders", service.getAllOrders());
    }
}
