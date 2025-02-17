package org.zerock.projects.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.zerock.projects.dto.MaterialDTO;
import org.zerock.projects.service.MaterialService;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/materials")
public class MaterialController {

    private final MaterialService materialService;

    // 자재 목록을 보여주는 기본 메서드
    @GetMapping
    public String showMaterialPage(@RequestParam(value = "searchType", required = false) String searchType,
                                   @RequestParam(value = "searchInput", required = false) String searchInput,
                                   Pageable pageable, Model model) {

        // searchMaterials 메서드를 통해 자재 검색
        Page<MaterialDTO> materials = materialService.searchMaterials(searchType, searchInput, pageable);

        // 모델에 자재 목록과 검색 조건을 추가
        model.addAttribute("materials", materials.getContent());
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchInput", searchInput);
        model.addAttribute("currentPage", materials.getNumber());
        model.addAttribute("totalPages", materials.getTotalPages());
        model.addAttribute("totalItems", materials.getTotalElements());

        return "material"; // 자재 목록 페이지
    }

    // 자재 목록을 보여주는 기본 메서드 (페이지 목록)
    @GetMapping("/material-list")
    public String list(Model model) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MaterialDTO> materials = materialService.getAllMaterials(pageable);

        // 모델에 필요한 페이지 정보 추가
        model.addAttribute("materials", materials.getContent());
        model.addAttribute("currentPage", materials.getNumber());
        model.addAttribute("totalPages", materials.getTotalPages());
        model.addAttribute("totalItems", materials.getTotalElements());
        model.addAttribute("hasPrevious", materials.hasPrevious());
        model.addAttribute("hasNext", materials.hasNext());

        return "material"; // 자재 목록 페이지
    }

    // 검색 기능을 포함한 메서드
    @GetMapping("/search")
    public String search(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "type", required = false, defaultValue = "") String type,
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, 10);

        // 검색 조건에 맞는 자재 리스트 조회
        Page<MaterialDTO> materials = getMaterialsBySearchCriteria(type, keyword, pageable);

        // 모델에 필요한 페이지 정보 추가
        model.addAttribute("materials", materials.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", materials.getTotalPages());
        model.addAttribute("totalItems", materials.getTotalElements());
        model.addAttribute("searchType", type);
        model.addAttribute("searchInput", keyword);

        return "material"; // 자재 목록 페이지
    }

    // 검색 조건에 맞는 자재 목록 조회
    private Page<MaterialDTO> getMaterialsBySearchCriteria(String type, String keyword, Pageable pageable) {
        if (type == null || keyword == null || type.isEmpty() || keyword.isEmpty()) {
            // 검색 조건이 없으면 모든 자재를 반환
            return materialService.getAllMaterials(pageable);
        }

        // 검색 조건에 따라 필터링
        switch (type) {
            case "mid":
                return materialService.getMaterialsByMid(keyword, pageable);
            case "mname":
                return materialService.getMaterialsByMname(keyword, pageable);
            case "mcategory":
                return materialService.getMaterialsByMcategory(keyword, pageable);
            case "mprice":
                try {
                    Double mprice = Double.parseDouble(keyword);
                    return materialService.getMaterialsByMprice(mprice, pageable);
                } catch (NumberFormatException e) {
                    return Page.empty(pageable);  // 숫자가 아닌 경우 빈 페이지 반환
                }
            case "mwarehouse":
                return materialService.getMaterialsByMwarehouse(keyword, pageable);
            case "mstockstatus":
                return materialService.getMaterialsByMstockstatus(keyword, pageable);
            default:
                return materialService.getAllMaterials(pageable);  // 기본적으로 모든 자재 반환
        }
    }
}
