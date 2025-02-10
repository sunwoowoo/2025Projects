package org.zerock.projects.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.projects.domain.Material;

public interface MaterialRepository extends JpaRepository<Material, String> {

}
