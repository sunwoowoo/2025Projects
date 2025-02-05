package org.zerock.projects.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.projects.domain.OrderStatus;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.ProcessType;
import org.zerock.projects.domain.machines.PressTask;
import org.zerock.projects.domain.machines.TaskStatus;
import org.zerock.projects.repository.machines.TaskRepository;
import org.zerock.projects.service.machines.ProcessService;

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

    @Test
    public void testCreateOrder() {
        ProductionOrder order = productionOrderService.createOrder("Model S", 10);
        assertNotNull(order.getId());
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());
    }

    @Test
    public void testAssignToProcess() {
        ProductionOrder order = productionOrderService.getOneOrder(1L)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        processService.assignToProcess(order, ProcessType.PRESSING);

        List<PressTask> pressTasks = taskRepository.findByProcessProcessType(ProcessType.PRESSING);
        assertFalse(pressTasks.isEmpty());
        assertEquals(TaskStatus.PENDING, pressTasks.get(0).getTaskStatus());
    }
}
