package org.zerock.projects.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zerock.projects.dto.MaterialDTO;
import org.zerock.projects.service.MaterialService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/materials")
public class MaterialController {

    private final MaterialService materialService;

    //html전달
    @GetMapping("/material")
    public String list(Model model){
        List<MaterialDTO> materials = materialService.getAllMaterials();  // 데이터 가져오기

        model.addAttribute("materials",materials);  // 모델에 자재 리스트 추가

        return "/material";
    }
}
