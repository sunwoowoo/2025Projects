package org.zerock.projects.service.machines;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.service.ProductionOrderService;

import java.util.List;

@Service
public class ProcessScheduler {     // 자동 공정 스케줄 설정. 수정 필요
    @Autowired
    private ProductionOrderService orderService;

    @Scheduled(fixedRate = 60000) // Run every 60 seconds
    public void checkAndMoveOrders() {
        List<ProductionOrder> orders = orderService.getOrdersInProgress();
        for (ProductionOrder order : orders) {
            if (isProcessComplete(order)) {
                orderService.moveToNextProcess(order);
            }
        }
    }

    private boolean isProcessComplete(ProductionOrder order) {  // 공정단계 완료 시
        return true;
    }
}
