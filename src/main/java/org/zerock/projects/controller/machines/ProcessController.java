//package org.zerock.projects.controller.machines;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.zerock.projects.domain.machines.Process;
//
//import java.util.List;
//
//@Controller
//@Log4j2
//@RequiredArgsConstructor
//public class ProcessController {
//    @Autowired
//    ProcessSer
//
//    private final ProcessService processService;
//
//    @GetMapping("/process")
//    public String process(Model model) {
//        // 공정 목록 가져오기
//        List<Process> processes = processService.getAllProcesses();
//
//        // 모델에 추가하여 뷰에서 사용할 수 있게 설정
//        model.addAttribute("processes", processes);
//
//        // "process" 뷰를 반환 (resources/templates/process.html과 연결되어야 함)
//        return "process";
//    }
//}
