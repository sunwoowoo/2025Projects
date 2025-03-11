package org.zerock.projects.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.projects.domain.Material;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.dto.MaterialDTO;
import org.zerock.projects.dto.ProductionOrderDTO;
import org.zerock.projects.repository.MaterialRepository;

import javax.persistence.EntityNotFoundException;
import java.util.*;
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

    public MaterialDTO readOne(Long mid) {
        return materialRepository.findById(mid)
                .map(MaterialDTO::fromEntity2)
                .orElseThrow(() -> new EntityNotFoundException("Material not found with ID: " + mid));
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

    public Page<Material> getAllOrders(Pageable pageable) {
        return materialRepository.findAll(pageable);
    }

    // 주문 삭제
    @Transactional
    public void removeMaterial(Long mid) {
        materialRepository.deleteRelatedMaterialConsumptions(Collections.singletonList(mid));
        materialRepository.deleteByMid(mid);
    }

    @Transactional
    public void removeMaterials(List<Long> materialIds) {
        materialRepository.deleteRelatedMaterialConsumptions(materialIds);
        materialRepository.deleteAllById(materialIds);
    }

    //팝업주문 저장
    public void saveMaterial(Material material){

        materialRepository.save(material);
    }

    // 재고 상태가 'in-stock'인 자재 리스트 반환
    public List<Material> getInStockMaterials() {
        List<Material> materials = materialRepository.findByMstockstatus("in-stock");
        return (materials != null && !materials.isEmpty()) ? materials : null;  // 빈 리스트도 null로 처리
    }

    // 재고 상태가 'out-stock'인 자재 리스트 반환
    public List<Material> getOutStockMaterials() {
        List<Material> materials = materialRepository.findByMstockstatus("out-stock");
        return (materials != null && !materials.isEmpty()) ? materials : null;  // 빈 리스트도 null로 처리
    }

    // 재고 상태가 'in-stock'인 자재 개수 반환
    public Long getInStockCount() {
        return materialRepository.countByMstockstatus("in-stock");
    }

    // 재고 상태가 'out-stock'인 자재 개수 반환
    public Long getOutStockCount() {
        return materialRepository.countByMstockstatus("out-stock");
    }

    // 모든 자재
    public List<Material> getAll() {return materialRepository.findAll();}
}

