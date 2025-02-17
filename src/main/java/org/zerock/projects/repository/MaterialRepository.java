package org.zerock.projects.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.projects.domain.Material;

public interface MaterialRepository extends JpaRepository<Material, String> {
    Page<Material> findByMidContaining(String mid, Pageable pageable);

    Page<Material> findByMnameContaining(String mname, Pageable pageable);

    Page<Material> findByMcategoryContaining(String mcategory, Pageable pageable);

    Page<Material> findByMquantity(Integer mquantity, Pageable pageable);

    Page<Material> findByMprice(Double mprice, Pageable pageable);

    Page<Material> findByMwarehouseContaining(String mwarehouse, Pageable pageable);

    Page<Material> findByMstockstatusContaining(String mstockstatus, Pageable pageable);
}

