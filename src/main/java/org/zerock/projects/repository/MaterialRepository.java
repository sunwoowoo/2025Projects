package org.zerock.projects.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.projects.domain.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    // 여러 개의 자재명을 기준으로 조회
    List<Material> findBymnameIn(List<String> mnames);

    // 특정 키워드가 포함된 자재명 모델을 검색하고, OrderId 기준으로 내림차순 정렬
    Page<Material> findBymnameContainingOrderByMidDesc(String keyword, Pageable pageable);

    // 특정 키워드가 포함된 자재명을 검색 (LIKE 사용)
    @Query("SELECT m FROM Material m WHERE m.mname LIKE CONCAT('%', :keyword, '%')")
    Page<Material> findKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 재고 상태가 'in-stock'인 자재 조회
    List<Material> findByMstockstatus(String mstockstatus);

    // 재고 상태가 'in-stock'인 자재의 개수
    Long countByMstockstatus(String mstockstatus);

    void deleteByMid(Long mid);

    @Modifying
    @Query("DELETE FROM MaterialConsumption mc WHERE mc.material.mid IN :materialIds")
    void deleteRelatedMaterialConsumptions(@Param("materialIds") List<Long> materialIds);
}
