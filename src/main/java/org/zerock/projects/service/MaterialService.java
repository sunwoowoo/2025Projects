package org.zerock.projects.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.projects.dto.MaterialDTO;
import org.zerock.projects.repository.MaterialRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;

    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    // 모든 자재를 조회하는 메서드
    public Page<MaterialDTO> getAllMaterials(Pageable pageable) {
        return materialRepository.findAll(pageable)
                .map(MaterialDTO::fromEntity);
    }

    // 검색 통합 메서드
    public Page<MaterialDTO> searchMaterials(String searchType, String searchInput, Pageable pageable) {
        if (searchType == null || searchInput == null || searchInput.isEmpty()) {
            return getAllMaterials(pageable);
        }

        switch (searchType) {
            case "mid":
                return getMaterialsByMid(searchInput, pageable);
            case "mname":
                return getMaterialsByMname(searchInput, pageable);
            case "mcategory":
                return getMaterialsByMcategory(searchInput, pageable);
            case "mprice":
                try {
                    Double mprice = Double.parseDouble(searchInput);
                    return getMaterialsByMprice(mprice, pageable);
                } catch (NumberFormatException e) {
                    return Page.empty(pageable); // 숫자가 아닌 경우 빈 페이지 반환
                }
            case "mwarehouse":
                return getMaterialsByMwarehouse(searchInput, pageable);
            case "mstockstatus":
                return getMaterialsByMstockstatus(searchInput, pageable);
            default:
                return getAllMaterials(pageable);
        }
    }

    // mid로 검색
    public Page<MaterialDTO> getMaterialsByMid(String mid, Pageable pageable) {
        return materialRepository.findByMidContaining(mid, pageable)
                .map(MaterialDTO::fromEntity);
    }

    // mname으로 검색
    public Page<MaterialDTO> getMaterialsByMname(String mname, Pageable pageable) {
        return materialRepository.findByMnameContaining(mname, pageable)
                .map(MaterialDTO::fromEntity);
    }

    // mcategory로 검색
    public Page<MaterialDTO> getMaterialsByMcategory(String mcategory, Pageable pageable) {
        return materialRepository.findByMcategoryContaining(mcategory, pageable)
                .map(MaterialDTO::fromEntity);
    }

    // mquantity로 검색
    public Page<MaterialDTO> getMaterialsByMquantity(Integer mquantity, Pageable pageable) {
        return materialRepository.findByMquantity(mquantity, pageable)
                .map(MaterialDTO::fromEntity);
    }

    // mprice로 검색
    public Page<MaterialDTO> getMaterialsByMprice(Double mprice, Pageable pageable) {
        return materialRepository.findByMprice(mprice, pageable)
                .map(MaterialDTO::fromEntity);
    }

    // mwarehouse로 검색
    public Page<MaterialDTO> getMaterialsByMwarehouse(String mwarehouse, Pageable pageable) {
        return materialRepository.findByMwarehouseContaining(mwarehouse, pageable)
                .map(MaterialDTO::fromEntity);
    }

    // mstockstatus로 검색
    public Page<MaterialDTO> getMaterialsByMstockstatus(String mstockstatus, Pageable pageable) {
        return materialRepository.findByMstockstatusContaining(mstockstatus, pageable)
                .map(MaterialDTO::fromEntity);
    }
}
