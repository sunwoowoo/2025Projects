package org.zerock.projects.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.projects.dto.MaterialDTO;
import org.zerock.projects.repository.MaterialRepository;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
public class MaterialService {
    private final MaterialRepository materialRepository;

    public MaterialService(MaterialRepository materialRepository){
        this.materialRepository = materialRepository;
    }

    //모든 자재정보를 가져오는 메서드
    public List<MaterialDTO> getAllMaterials(){
        return materialRepository.findAll().stream()
                .map(MaterialDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
