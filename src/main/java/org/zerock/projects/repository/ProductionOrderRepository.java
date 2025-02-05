package org.zerock.projects.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.projects.domain.OrderStatus;
import org.zerock.projects.domain.ProductionOrder;

import java.util.List;

public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {
    // 주문제품상태별로 주문제품 찾기
    List<ProductionOrder> findByOrderStatus(OrderStatus orderStatus);
}
