package org.zerock.projects.controller.subprocesses;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.zerock.projects.dto.PaintingDTO;
import org.zerock.projects.service.subprocesses.PaintingService;

@Controller
@Log4j2
@RequiredArgsConstructor
public class PaintingController {

    private final PaintingService paintingService;
    @GetMapping("/painting/{pno}")
    public String painting(@PathVariable("pno") Long pno, Model model) {
        PaintingDTO paintingDTO= paintingService. findPaintingByPno(pno);
        if (paintingDTO == null) {
            log.error("Painting not found by pno: {}", pno);
            return "error/404";
        }
        model.addAttribute("painting", paintingDTO);
        return "painting";
    }
    @PostMapping("/painting/update")
    public ResponseEntity<String>  updatePainting(@RequestParam Long pno,
                                                  @RequestParam boolean preprocessing,
                                                  @RequestParam  boolean electrodeposition,
                                                  @RequestParam  boolean sealing ,
                                                  @RequestParam boolean MTprocesses) {
            paintingService.updatePainting(pno, preprocessing, electrodeposition, sealing, MTprocesses);
            return ResponseEntity.ok("Painting updated");

    }

}
