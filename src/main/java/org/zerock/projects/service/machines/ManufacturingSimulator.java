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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import static org.zerock.projects.domain.machines.TaskType.COMPLETED;
import static org.zerock.projects.domain.machines.TaskType.getTasksForProcess;

@Log4j2
@Component
public class ManufacturingSimulator {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProductionOrderRepository productionOrderRepository;

    @Autowired
    private ProcessRepository processRepository;

    // 진행률 업데이트
    public void updateTaskProgress(Task task) {
        log.info("Starting updateTaskProgress for task type: {}, current progress: {}",
                task.getTaskType(), task.getProgress());

        // Add randomization to progress increment
        Random random = new Random();
        int increment = random.nextInt(100) + 50;
        int newProgress = Math.min(task.getProgress() + increment, 100);

        log.info("Increment: {}, New progress: {}", increment, newProgress);

        task.setProgress(newProgress);
        taskRepository.save(task);

        if (newProgress == 100) {
            log.info("Task reached 100%. Marking as completed.");
            task.setCompleted(true);
            taskRepository.save(task);  // Save the completed task first
        }
    }

    // 주문제작 실시
    public void simulateProductionOrder(ProductionOrder order) {
        productionOrderRepository.save(order);
        List<Process> processes = createProcesses(order);

        for (Process process : processes) {
            log.info("Process phase : {}", process.getType());
            order.setOrderStatus(OrderStatus.IN_PROGRESS);
            order.setProcessType(process.getType());
            order.setStartDate(LocalDate.now());
            productionOrderRepository.save(order);

            for (Task task : process.getTasks()) {
                while (!task.isCompleted()) {
                    updateTaskProgress(task);
                    order.setProgress(calculateOverallProgress(order));

                    if (task.isCompleted()) {
                        log.info("Task {} completed. Moving to next task.", task.getTaskType());
                        break;  // Exit the while loop and move to the next task
                    }

                    try {
                        Thread.sleep(3000);  // Simulate time passing between progress updates
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.error("Simulation interrupted", e);
                    }
                }
            }
            log.info("Process {} is Completed", process.getType());
        }
        order.setOrderStatus(OrderStatus.COMPLETED);
        order.setEndDate(LocalDate.now());
        order.setProcessType(ProcessType.COMPLETED);
        productionOrderRepository.save(order);
    }

    // 주문제품에 모든 공정과정 저장
    public List<Process> createProcesses(ProductionOrder order) {
        productionOrderRepository.save(order);
        List<Process> processes = new ArrayList<>();

        for (ProcessType processType : ProcessType.values()) {
            if (processType != ProcessType.COMPLETED) {
                Process process = Process.builder()
                        .type(processType)
                        .productionOrder(order)
                        .build();

                List<Task> tasks = new ArrayList<>();
                List<TaskType> taskTypes = getTasksForProcess(process);
                processRepository.save(process);

                for (int i = 0; i < taskTypes.size(); i++) {
                    Task task = Task.builder()
                            .taskType(taskTypes.get(i))
                            .process(process)
                            .build();
                    tasks.add(task);
                    taskRepository.save(task);
                }

                process.setTasks(tasks);
                processes.add(process);
            }
        }
        return processes;
    }

    public double calculateOverallProgress(ProductionOrder productionOrder) {
        List<Process> processes = productionOrder.getProcesses();

        if (processes == null || processes.isEmpty()) {
            return 0.0;
        }

        int totalTasks = 0;
        double totalProgress = 0.0;

        for (Process process : processes) {
            for (Task task : process.getTasks()) {
                totalTasks++;
                totalProgress += task.getProgress();
            }
        }

        if (totalTasks == 0) {
            return 0.0;
        }

        return (totalProgress / (totalTasks * 100)) * 100;
    }

}