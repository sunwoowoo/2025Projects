package org.zerock.projects.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.projects.domain.Material;
import org.zerock.projects.dto.MaterialDTO;
import org.zerock.projects.service.MaterialService;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/materials")
public class MaterialController {

    private final MaterialService materialService;

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "errorPage";
    }

    @GetMapping  // URL: /materials
    public String list(Model model) {
        List<MaterialDTO> materials = materialService.getAllMaterials();
        model.addAttribute("materials", materials);
        return "material";
    }

    @GetMapping("/{orderId}")  // URL: /materials/{orderId}
    public String read(@PathVariable Long orderId, Model model) {

        // 자재 정보를 조회
        MaterialDTO materialDTO = materialService.readOne2(orderId);

        // 로그 추가 (materialDTO의 값 확인)
        log.info("MaterialDTO: {}", materialDTO);

        // 창고 및 카테고리 목록 정의
        List<String> availableWarehouse = Arrays.asList("A-1", "A-2", "A-3");
        List<String> availableCategory = Arrays.asList("재료", "완성품목", "타이어", "조립품목");

        log.info("Material: {}", materialDTO);  // mread 객체의 값 확인
        log.info("Available Categories: {}", availableCategory);
        log.info("Available Warehouses: {}", availableWarehouse);

        // 모델에 데이터 추가
        model.addAttribute("availableWarehouse", availableWarehouse);
        model.addAttribute("availableCategory", availableCategory);
        model.addAttribute("mread", materialDTO);

        return "mread";  // Thymeleaf 템플릿 이름
    }

    @GetMapping("/material_modify/{orderId}")   // 자재 수정 페이지
    public String modifyOrder2(@PathVariable Long orderId, Model model) {
        try {
            // 자재 정보 조회
            Material material = materialService.getOrderById(orderId);
            MaterialDTO materialDTO = MaterialDTO.fromEntity2(material);
            model.addAttribute("mread", materialDTO);
            return "material_modify";  // 수정 페이지로 리턴
        } catch (Exception e) {
            log.error("Error occurred while fetching order with ID: {}", orderId, e);
            return "error"; // error.html로 페이지를 리턴하거나 적절한 처리를 합니다.
        }
    }
}
