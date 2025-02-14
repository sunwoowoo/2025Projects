package org.zerock.projects.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.projects.domain.Material;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.dto.MaterialDTO;
import org.zerock.projects.dto.ProductionOrderDTO;
import org.zerock.projects.repository.MaterialRepository;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                .map(MaterialDTO::fromEntity2)
                .collect(Collectors.toList());
    }

    public MaterialDTO readOne2(Long orderId) {
        return materialRepository.findById(orderId)
                .map(MaterialDTO::fromEntity2)
                .orElseThrow(() -> new EntityNotFoundException("Material not found with ID: " + orderId));
    }

    public Material getOrderById(Long mid) {
        return materialRepository.findById(mid).orElse(null);  // ID에 해당하는 주문을 찾고, 없으면 null 반환
    }

    @Transactional
    public void updateOrder(Long orderId, MaterialDTO materialDTO) {
        // 주문을 ID로 찾아오기
        Material material = materialRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found:"+ orderId));

        // 주문 정보 업데이트 (carModel , quantity)
        if (materialDTO.getMid() != null) {
            material.setMid(materialDTO.getMid());
        }
        if (materialDTO.getMname() != null) {
            material.setMname(materialDTO.getMname());
        }
        if (materialDTO.getMcategory() != null) {
            material.setMcategory(materialDTO.getMcategory());
        }
        if (materialDTO.getMquantity() != null) {
            material.setMquantity(materialDTO.getMquantity());
        }
        if (materialDTO.getMprice() != null) {
            material.setMprice(materialDTO.getMprice());
        }
        if (materialDTO.getMwarehouse() != null) {
            material.setMwarehouse(materialDTO.getMwarehouse());
        }
        if (materialDTO.getMstockstatus() != null) {
            material.setMstockstatus(materialDTO.getMstockstatus());
        }

    }

    public Map<String, Integer> getMaterialQuantities() {
        List<Object[]> resultList = materialRepository.findMaterialQuantities();

        Map<String, Integer> materialData = new HashMap<>();
        for (Object[] row : resultList) {
            String mid = (String) row[0]; // Material ID
            Integer quantity = ((Number) row[1]).intValue(); // Material Quantity
            materialData.put(mid, quantity);
        }
        return materialData;
    }


}
