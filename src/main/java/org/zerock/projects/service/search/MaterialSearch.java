package org.zerock.projects.service.search;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.projects.domain.Material;
import org.zerock.projects.repository.MaterialRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialSearch {

    private final MaterialRepository materialRepository;

    // 여러 개의 자재명을 기준으로 조회
    public List<Material> searchByMaterialNames(List<String> mnames) {
        return materialRepository.findBymnameIn(mnames);  // materialRepository 사용
    }

    // 특정 키워드가 포함된 차량 모델을 검색하고, OrderId 기준으로 내림차순 정렬 (페이징 포함)
    public Page<Material> searchByMaterialKeyword(String keyword, Pageable pageable) {
        return materialRepository.findBymnameContainingOrderByMidDesc(keyword, pageable);
    }

    // 특정 키워드가 포함된 차량 모델을 검색 (LIKE 사용, 페이징 포함)
    public Page<Material> searchByKeyword(String keyword, Pageable pageable) {
        return materialRepository.findKeyword(keyword, pageable);
    }

    //mid 값으로 자재 삭제
    public void deleteMaterialByMid(Long mid){
        materialRepository.deleteByMid(mid);
    }
}
