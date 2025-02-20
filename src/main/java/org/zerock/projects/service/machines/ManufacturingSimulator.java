package org.zerock.projects.service.machines;

import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.projects.domain.Material;
import org.zerock.projects.domain.MaterialConsumption;
import org.zerock.projects.domain.OrderStatus;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.Process;
import org.zerock.projects.domain.machines.ProcessType;
import org.zerock.projects.domain.machines.Task;
import org.zerock.projects.domain.machines.TaskType;
import org.zerock.projects.repository.MaterialRepository;
import org.zerock.projects.repository.ProductionOrderRepository;
import org.zerock.projects.repository.machines.ProcessRepository;
import org.zerock.projects.repository.machines.TaskRepository;
import org.zerock.projects.domain.machines.TaskType;
import org.zerock.projects.repository.subprocesses.MaterialConsumptionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private MaterialConsumptionRepository materialConsumptionRepository;

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

        // Material Consumption Logic
        List<MaterialConsumption> consumptions = simulateMaterialConsumption(order);

        // Update material quantities
        for (MaterialConsumption consumption : consumptions) {
            Material material = consumption.getMaterial();
            material.setMquantity(material.getMquantity() - consumption.getQuantityUsed());
            materialRepository.save(material);
            materialConsumptionRepository.save(consumption); // Save the consumption record
        }

        order.getMaterialConsumptions().addAll(consumptions);
        order.setOrderStatus(OrderStatus.COMPLETED);
        order.setEndDate(LocalDate.now());
        order.setProcessType(ProcessType.COMPLETED);

        // Calculate and set the production cost
        double productionCost = calculateProductionCost(order);
        order.setProductionCost(productionCost);

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

    // 전체 진행률 계산 method
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

    // 한 대당 소모되는 재료들 모음
    private List<MaterialConsumption> simulateMaterialConsumption(ProductionOrder order) {
        List<MaterialConsumption> consumptions = new ArrayList<>();
        List<Material> materials = materialRepository.findAll();

        for (ProcessType processType : ProcessType.values()) {
            List<Material> processMaterials = materials.stream()
                    .filter(m -> m.getMprocess() == processType)
                    .collect(Collectors.toList());

            for (Material material : processMaterials) {
                int quantityUsed = calculateQuantityUsed(material, order.getQuantity());
                MaterialConsumption consumption = MaterialConsumption.builder()
                        .productionOrder(order)
                        .material(material)
                        .quantityUsed(quantityUsed)
                        .processType(processType)
                        .consumptionDate(LocalDateTime.now())
                        .build();
                consumptions.add(consumption);
            }
        }
        return consumptions;
    }

    // 차 한 대당 재료별 소모량 계산
    private int calculateQuantityUsed(Material material, int orderQuantity) {
        switch (material.getMname()) {
            case "Steel Sheet":
            case "Aluminum Panel":
                return 2 * orderQuantity; // 2 sheets per car
            case "Rubber Seal":
                return 10 * orderQuantity; // 10 seals per car
            case "Windshield":
                return orderQuantity; // 1 windshield per car
            case "Paint - Red":
            case "Paint - Blue":
                return (int) (2.5 * orderQuantity); // 2.5 units of paint per car
            case "Welding Rod":
                return 50 * orderQuantity; // 50 welding rods per car
            case "Engine Block":
            case "Transmission":
                return orderQuantity; // 1 per car
            case "Seat Fabric":
                return 5 * orderQuantity; // 5 units per car (for multiple seats)
            case "Headlight Assembly":
                return 2 * orderQuantity; // 2 per car
            case "Tire":
                return 4 * orderQuantity; // 4 tires per car
            case "Brake Pad":
                return 4 * orderQuantity; // 4 brake pads per car
            case "Steering Wheel":
                return orderQuantity; // 1 per car
            case "Catalytic Converter":
                return orderQuantity; // 1 per car
            case "Door Handle":
                return 4 * orderQuantity; // 4 door handles per car
            case "Airbag":
                return 6 * orderQuantity; // Assuming 6 airbags per car
            case "Wiring Harness":
                return orderQuantity; // 1 main harness per car
            case "Shock Absorber":
                return 4 * orderQuantity; // 4 shock absorbers per car
            case "Fuel Injector":
                return 4 * orderQuantity; // Assuming 4-cylinder engine
            default:
                return (int) (0.1 * material.getMquantity() * orderQuantity); // Default calculation
        }
    }

    // 원가 계산
    public double calculateProductionCost(ProductionOrder order) {
        double totalCost = 0.0;
        List<Material> materials = materialRepository.findAll();

        for (Material material : materials) {
            int quantityUsed = calculateQuantityUsed(material, order.getQuantity());
            double materialCost = quantityUsed * material.getMprice();
            totalCost += materialCost;
        }

        // Add a fixed cost for labor and overhead
        double laborAndOverheadCost = 5000.0 * order.getQuantity(); // Assuming $5000 per car for labor and overhead
        totalCost += laborAndOverheadCost;

        return totalCost;
    }

}
