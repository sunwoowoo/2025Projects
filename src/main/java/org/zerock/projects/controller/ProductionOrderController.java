package org.zerock.projects.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.zerock.projects.service.ProductionOrderService;

@Controller
public class ProductionOrderController {
    @Autowired
    private ProductionOrderService service;

    @GetMapping("/list")
    public String getAllOrders(Model model) {
        model.addAttribute("production_order", service.getAllOrders());
        return "list";
    }
}
