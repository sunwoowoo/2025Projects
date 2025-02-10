package org.zerock.projects.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.projects.domain.OrderStatus;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.Process;
import org.zerock.projects.domain.machines.ProcessType;
import org.zerock.projects.domain.machines.Task;
import org.zerock.projects.repository.ProductionOrderRepository;
import org.zerock.projects.repository.machines.ProcessRepository;
import org.zerock.projects.repository.machines.TaskRepository;
import org.zerock.projects.service.machines.ManufacturingSimulator;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
@SpringBootTest
public class ManufacturingSimulatorTest {
    @Autowired
    private ManufacturingSimulator simulator;

    @Autowired
    private ProductionOrderRepository orderRepository;

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void testUpdateTaskProgress() {
        // Create a test production order
        ProductionOrder order = new ProductionOrder();
        order.setCarModel("Test Model");
        order.setOrderStatus(OrderStatus.IN_PROGRESS);
        order.setProcessType(ProcessType.PRESSING);
        order.setStartDate(LocalDate.now());
        orderRepository.save(order);
        log.info("Created production order with ID: {}", order.getId());

        // Create a process
        Process process = new Process();
        process.setType(ProcessType.PRESSING);
        process.setProductionOrder(order);
        processRepository.save(process);
        log.info("Created process with ID: {}", process.getId());

        // Create initial task (SHEARING)
        Task task = new Task();
        task.setTaskType(TaskType.SHEARING);
        task.setProcess(process);
        task.setProgress(95); // Set high progress to test completion
        Task savedTask = taskRepository.save(task);
        log.info("Created initial task with ID: {}", savedTask.getId());

        // Run the simulator
        simulator.updateTaskProgress(task);

        // Verify the results
        Task updatedTask = taskRepository.findById(task.getId()).get();
        log.info("Updated task - progress: {}, completed: {}",
                updatedTask.getProgress(), updatedTask.isCompleted());

        List<Task> allTasks = taskRepository.findByProcess(process);
        log.info("Found {} tasks after update", allTasks.size());
        allTasks.forEach(t -> log.info("Task type: {}, progress: {}, completed: {}",
                t.getTaskType(), t.getProgress(), t.isCompleted()));

        assertTrue(updatedTask.isCompleted(),
                "Task should be completed. Current progress: " + updatedTask.getProgress());
        assertEquals(100, updatedTask.getProgress(),
                "Progress should be 100");

        // Verify next task was created (BENDING)
        Optional<Task> nextTask = allTasks.stream()
                .filter(t -> t.getTaskType() == TaskType.BENDING)
                .findFirst();

        assertTrue(nextTask.isPresent(),
                "Next task (BENDING) should be created. Found tasks: " +
                        allTasks.stream()
                                .map(t -> t.getTaskType() + ":" + t.getProgress())
                                .collect(Collectors.joining(", ")));
    }
}
