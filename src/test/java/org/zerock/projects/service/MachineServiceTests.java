package org.zerock.projects.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.projects.domain.OrderStatus;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.ProcessType;
import org.zerock.projects.domain.machines.Task;
import org.zerock.projects.domain.machines.TaskStatus;
import org.zerock.projects.repository.ProductionOrderRepository;
import org.zerock.projects.repository.machines.TaskRepository;
import org.zerock.projects.service.machines.ProcessService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
public class MachineServiceTests {
    @Autowired
    ProductionOrderService productionOrderService;

    @Autowired
    ProcessService processService;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    ProductionOrderRepository productionOrderRepository;

    @Test       // 1개 주문 넣기
    public void testCreateOrder() {
        ProductionOrder order = productionOrderService.createOrder("Model Y", 10);
        assertNotNull(order.getId());
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());
    }

    @Test       // 제작 시작
    public void testAssignToProcess() {
        ProductionOrder order = productionOrderService.getOneOrder(6L)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        processService.assignToProcess(order, ProcessType.PRESSING);

        List<Task> tasks = taskRepository.findByProcessProcessType(ProcessType.PRESSING);
        assertFalse(tasks.isEmpty());
        assertEquals(TaskStatus.PENDING, tasks.get(0).getTaskStatus());
    }

    @Test       // 다음 공정 넘어가기
    public void testMoveToNextProcess() {
        ProductionOrder order = productionOrderRepository.findById(6L).orElseThrow();
        productionOrderService.moveToNextProcess(order);
    }
}
