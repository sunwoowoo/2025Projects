package org.zerock.projects.repository.subprocesses;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.projects.domain.MaterialConsumption;

public interface MaterialConsumptionRepository extends JpaRepository<MaterialConsumption, Long> {
}
