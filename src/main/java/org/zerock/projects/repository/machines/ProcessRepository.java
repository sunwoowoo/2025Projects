package org.zerock.projects.repository.machines;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.Process;
import org.zerock.projects.domain.machines.ProcessType;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessRepository extends JpaRepository<Process, Long> {
    Process findByProductionOrderAndType(ProductionOrder order, ProcessType processType);
}
