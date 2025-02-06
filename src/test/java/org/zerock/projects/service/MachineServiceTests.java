package org.zerock.projects.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.projects.domain.OrderStatus;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.Process;
import org.zerock.projects.domain.machines.ProcessType;
import org.zerock.projects.domain.machines.Task;
import org.zerock.projects.domain.machines.TaskStatus;
import org.zerock.projects.repository.ProductionOrderRepository;
import org.zerock.projects.repository.machines.TaskRepository;
import org.zerock.projects.service.machines.MachineSimulator;
import org.zerock.projects.service.machines.ProcessScheduler;
import org.zerock.projects.service.machines.ProcessService;
import org.zerock.projects.service.machines.TaskService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
public class MachineServiceTests {
    @Autowired
    private ProductionOrderRepository productionOrderRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProcessService processService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProcessScheduler processScheduler;

    @Autowired
    private MachineSimulator machineSimulator;

    @Autowired
    private ProductionOrderService productionOrderService;

    @Test       // 1개 주문 넣기
    public void testCreateOrder() {
        ProductionOrder order = productionOrderService.createOrder("Model Y", 10);
        assertNotNull(order.getId());
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());
    }

    @Test       // 제작 시작
    public void testAssignToProcess() {
        ProductionOrder order = productionOrderService.getOneOrder(8L)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        processService.assignToProcess(order, ProcessType.PRESSING);

        List<Task> tasks = taskRepository.findByProcessProcessType(ProcessType.PRESSING);
        assertFalse(tasks.isEmpty());
        assertEquals(TaskStatus.PENDING, tasks.get(0).getTaskStatus());
    }

    @Test       // 다음 공정 넘어가기
    public void testMoveToNextProcess() {
        ProductionOrder order = productionOrderRepository.findById(8L).orElseThrow();
        productionOrderService.moveToNextProcess(order);
    }

    @Test
    @Transactional
    public void testProductionProgressWithSimulation() throws InterruptedException {
        Long orderId = 8L; // Use an existing order ID
        ProductionOrder order = productionOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // Step 1: Simulate machine operations and process progress
        for (int i = 0; i < 10; i++) { // Simulate 10 cycles
            machineSimulator.simulateMachineStatus();

            Process pressingProcess = processService.getCurrentProcess(order);
            List<Task> pressingTasks = pressingProcess.getTasks();

            for (Task task : pressingTasks) {
                // Simulate progress on each task
                int progress = Math.min(task.getProgress() + 10, 100);
                taskService.updateTaskProgress(task.getId(), progress);
            }

            // Allow time for scheduled tasks to run
            Thread.sleep(1000);

            processScheduler.checkAndMoveOrders();

            // Refresh the order from the database
            order = productionOrderRepository.findById(orderId).orElseThrow();

            // If the order has moved to the next process, break the loop
            if (order.getProcessType() != ProcessType.PRESSING) {
                break;
            }
        }
    }
}
