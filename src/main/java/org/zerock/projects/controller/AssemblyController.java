package org.zerock.projects.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.zerock.projects.dto.AssemblyDTO;
import org.zerock.projects.service.AssemblyService;

@Controller
@Log4j2
@RequiredArgsConstructor
public class AssemblyController {
    private  final AssemblyService assemblyService;

    @GetMapping("/assembly/{ano}")
    public String assembly(@PathVariable("ano") Long ano, Model model) {
        AssemblyDTO assemblyDTO = assemblyService.findAssemblyByAno(ano);
        if (assemblyDTO == null) {
            log.error("Assembly not found. ano : {}", ano);
            return "error/404"; // 에러 페이지로 이동
        }

        model.addAttribute("assembly", assemblyDTO);
        return "assembly";
    }
    @PostMapping("/assembly/update")
    public ResponseEntity<String> update(@RequestParam Long ano, @RequestParam boolean atE, @RequestParam boolean sm) {
        assemblyService.updateAssembly(ano, atE, sm);
        return ResponseEntity.ok("업데이트 성공");
    }
}
