package org.zerock.projects.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.projects.domain.OrderStatus;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.ProcessType;

import java.util.List;
import java.util.Optional;

public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {
}
