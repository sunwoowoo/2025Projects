package org.zerock.projects.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.projects.domain.Material;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    @Query("SELECT m.mname, SUM(m.mquantity) FROM Material m GROUP BY m.mname")
    List<Object[]> findMaterialQuantities();
}
