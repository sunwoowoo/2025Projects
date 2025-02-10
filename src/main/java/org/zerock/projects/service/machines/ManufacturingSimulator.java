package org.zerock.projects.service.machines;

import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.projects.domain.OrderStatus;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.Process;
import org.zerock.projects.domain.machines.ProcessType;
import org.zerock.projects.domain.machines.Task;
import org.zerock.projects.domain.machines.TaskType;
import org.zerock.projects.repository.ProductionOrderRepository;
import org.zerock.projects.repository.machines.ProcessRepository;
import org.zerock.projects.repository.machines.TaskRepository;
import org.zerock.projects.domain.machines.TaskType;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Component
public class ManufacturingSimulator {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProductionOrderRepository productionOrderRepository;

    @Autowired
    private ProcessRepository processRepository;

    public void updateTaskProgress(Task task) {
        log.info("Starting updateTaskProgress for task type: {}, current progress: {}",
                task.getTaskType(), task.getProgress());

        // Add randomization to progress increment
        Random random = new Random();
        int increment = random.nextInt(15);
        int newProgress = Math.min(task.getProgress() + increment, 100);

        log.info("Increment: {}, New progress: {}", increment, newProgress);

        task.setProgress(newProgress);
        taskRepository.save(task);

        if (newProgress == 100) {
            log.info("Task reached 100%. Marking as completed.");
            task.setCompleted(true);
            taskRepository.save(task);  // Save the completed task first

            if (task.getTaskType() != TaskType.COMPLETED) {
                // Find next task in sequence
                Process process = task.getProcess();
                List<TaskType> processTaskTypes = TaskType.getTasksForProcess(process);
                int currentIndex = processTaskTypes.indexOf(task.getTaskType());

                log.info("Current task index: {}, name : {}, Total tasks in process: {}",
                        currentIndex, processTaskTypes.get(currentIndex), processTaskTypes.size());

                if (currentIndex < processTaskTypes.size() - 1) {
                    TaskType nextTaskType = processTaskTypes.get(currentIndex + 1);
                    log.info("Creating next task of type: {}", nextTaskType);

                    // Create and save the next task
                    Task nextTask = new Task();
                    nextTask.setTaskType(nextTaskType);
                    nextTask.setProcess(process);
                    nextTask.setProgress(0);
                    nextTask.setCompleted(false);
                    taskRepository.save(nextTask);

                    log.info("New task created with ID: {}, Type : {}", nextTask.getId(), nextTask.getTaskType());
                } else {
                    taskRepository.save(task);
                }

                List<Task> allTasks = taskRepository.findByProcess(task.getProcess());
                log.info("All tasks for process after update:");
                allTasks.forEach(t -> log.info("Task type: {}, progress: {}, completed: {}",
                        t.getTaskType(), t.getProgress(), t.isCompleted()));
            }
        }
    }

    public void simulateProductionOrder(ProductionOrder order) {
        // Add validation at the start
        if (order.getProcessType() == null && order.getOrderStatus() == OrderStatus.PENDING) {
            order.setOrderStatus(OrderStatus.IN_PROGRESS);
            order.setProcessType(ProcessType.PRESSING);
            order.setStartDate(LocalDate.now());
            productionOrderRepository.save(order);
            log.info("Order ID {}'s Status is {} in {} process.", order.getId(), order.getOrderStatus(), order.getProcessType());
        } else {
            log.error("ProcessType is null for order ID: {}", order.getId());
            throw new IllegalStateException("ProcessType must be set before starting production");
        }

        log.info("Production started!");

        if (order.getProcesses() == null || order.getProcesses().isEmpty()) {
            log.info("No processes found. Creating initial process for order ID: {}", order.getId());

            // Create new process
            Process initialProcess = new Process();
            initialProcess.setType(order.getProcessType());  // Use the ProcessType from the order
            initialProcess.setProductionOrder(order);
            processRepository.save(initialProcess);

            // Create initial task
            Task initialTask = new Task();
            initialTask.setTaskType(TaskType.getTasksForProcess(initialProcess).get(0));
            initialTask.setProcess(initialProcess);
            initialTask.setProgress(0);
            initialTask.setCompleted(false);
            taskRepository.save(initialTask);

            // Add task to process
            initialProcess.getTasks().add(initialTask);
            processRepository.save(initialProcess);

            // Add process to order
            order.getProcesses().add(initialProcess);

            // Save the order to persist changes
            productionOrderRepository.save(order);
            log.info("Created initial process with ID: {} and initial task", initialProcess.getId());
        }

        for (Process process : order.getProcesses()) {
            log.info("Process ID: {}, Number of tasks: {}", process.getId(), process.getTasks().size());
            log.info("Current Process - ID: {}, Type: {}", process.getId(), process.getType());
            for (Task task : process.getTasks()) {
                log.info("Current Task: - ID: {}, Type: {}", task.getId(), task.getTaskType());
                while (!task.isCompleted()) {
                    updateTaskProgress(task);
                    log.info("Task started: ID: {}, Type: {}", task.getId(), task.getTaskType());
                    if (task.isCompleted()) {
                        log.info("Task {} Completed", task.getTaskType());
                        break;
                    }
                    try {
                        Thread.sleep(3000); // Simulate time passing
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        order.setOrderStatus(OrderStatus.COMPLETED);
        order.setEndDate(LocalDate.now());
        productionOrderRepository.save(order);
        log.info("Production is completed!");
    }
}