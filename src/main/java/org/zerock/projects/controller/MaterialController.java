package org.zerock.projects.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.projects.domain.Material;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.dto.MaterialDTO;
import org.zerock.projects.dto.ProductionOrderDTO;
import org.zerock.projects.repository.MaterialRepository;
import org.zerock.projects.service.MaterialService;
import org.zerock.projects.service.search.MaterialSearch;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/materials")
public class MaterialController {

    @Autowired
    MaterialSearch materialSearch;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialRepository materialRepository;

    // 자재 리스트 화면
    @GetMapping
    public String list(@RequestParam(required = false, defaultValue = "") String types,
                       @RequestParam(required = false, defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "1") int page,
                       Model model) {
        int pageSize = 10; // 한 페이지에 10개씩 표시
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Material> orderPage;
        // 검색 조건 처리
        if (types != null && keyword != null && !keyword.isEmpty()) {
            switch (types) {
                case "mname":
                    orderPage = materialSearch.searchByKeyword(keyword, pageable);
                    break;

                default:
                    orderPage = materialService.getAllOrders(pageable);
            }
        } else {
            orderPage = materialService.getAllOrders(pageable);
        }

        // DTO 변환 후 모델에 추가
        List<MaterialDTO> orders = orderPage.stream()
                .map(MaterialDTO::fromEntity2)
                .collect(Collectors.toList());

        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("types", types);
        model.addAttribute("keyword", keyword);

        return "material";
    }

    @PostMapping("/remove")
    public String removeMaterial(@RequestParam("mid") Long mid, RedirectAttributes redirectAttributes) {

        materialService.removeMaterial(mid);

        redirectAttributes.addFlashAttribute("result", "removed");

        return "redirect:/materials";
    }

    @PostMapping("/create")
    public String createMaterial(@Valid Material material, BindingResult bindingResult
            , RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()) {
            log.info("has errors..............");
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/materials/create";
        }

        materialService.saveMaterial(material);

        return "redirect:/materials";
    }

    @GetMapping("/read/{mid}")
    public String readMaterial(@PathVariable Long mid, Model model) {

        // 서비스 호출
        MaterialDTO materialDTO = materialService.readOne(mid);

        model.addAttribute("materialread", materialDTO);
        return "material-read";
    }

    @GetMapping("/modify/{mid}")
    public String modifyOrder(@PathVariable Long mid, Model model) {
        log.info("Received material ID: {}", mid);
        Material material = materialService.getOrderById(mid);
        MaterialDTO materialDTO = MaterialDTO.fromEntity2(material);
        model.addAttribute("materialread", materialDTO);
        return "material-modify";
    }

    @PostMapping("/modify/{mid}")
    public String updateOrder(@PathVariable Long mid, @ModelAttribute MaterialDTO materialDTO) {
        // 자재 수정 처리
        materialService.updateOrder(mid, materialDTO);

        // 수정 후 자재 상세 페이지로 리디렉션 (정확한 경로 설정)
        return "redirect:/materials/read/" + mid;
    }
}
