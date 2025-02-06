package org.zerock.projects.service.machines;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.zerock.projects.domain.OrderStatus;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.*;
import org.zerock.projects.domain.machines.Process;
import org.zerock.projects.repository.ProductionOrderRepository;
import org.zerock.projects.repository.machines.ProcessRepository;
import org.zerock.projects.repository.machines.TaskRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

@Service
public class ProcessService implements IProcessService {

    private final ProcessRepository processRepository;
    private final TaskRepository taskRepository;
    private final TaskService taskService;
    private final ProductionOrderRepository orderRepository;

    @Autowired
    public ProcessService(
            ProcessRepository processRepository,
            @Lazy TaskRepository taskRepository,
            TaskService taskService,
            ProductionOrderRepository orderRepository) {
        this.processRepository = processRepository;
        this.taskRepository = taskRepository;
        this.taskService = taskService;
        this.orderRepository = orderRepository;
    }

    //  모든 공정단계 찾기
    public List<Process> getAllProcesses() {
        return processRepository.findAll();
    }

    //  공정 시작
    public void assignToProcess(ProductionOrder order, ProcessType newProcessType) {
        order.setProgress(0);

        if (order.getOrderStatus() == OrderStatus.PENDING && newProcessType != ProcessType.PRESSING) {
            throw new IllegalStateException("A PENDING order must start with PRESSING process");
        }

        Process process = processRepository.findByProcessType(newProcessType)
                .orElseGet(() -> {
                    Process newProcess = new Process();
                    newProcess.setProcessType(newProcessType);
                    return processRepository.save(newProcess);
                });

        taskService.createTasksForProcess(order, process);

        if (newProcessType == ProcessType.ASSEMBLY) {
            order.setOrderStatus(OrderStatus.COMPLETED);
            order.setEndDate(LocalDate.now());
        } else {
            order.setOrderStatus(OrderStatus.IN_PROGRESS);
        }

        order.setProcessType(newProcessType);
        order.setStartDate(LocalDate.now());
        orderRepository.save(order);
    }

    //  다음 공정 타입 가져오기
    public ProcessType getNextProcessType(ProcessType currentProcess) {
        switch (currentProcess) {
            case PRESSING: return ProcessType.WELDING;
            case WELDING: return ProcessType.PAINTING;
            case PAINTING: return ProcessType.ASSEMBLY;
            case ASSEMBLY: return ProcessType.COMPLETED;
            case COMPLETED: return null;
            default: throw new IllegalStateException("Invalid process: " + currentProcess);
        }
    }

    //  공정에 새로운 Task 추가
    public void addTaskToProcess(Process process, TaskType taskType, String description, int duration) {
        Task task = Task.builder()
                .taskType(taskType)
                .taskStatus(TaskStatus.PENDING)
                .process(process)
                .description(description)
                .duration(duration)
                .progress(0)
                .build();

        process.getTasks().add(task);
        taskRepository.save(task);
        process.updateProgress();
        processRepository.save(process);
    }

    //  현재 공정 찾기
    public Process getCurrentProcess(ProductionOrder order) {
        return processRepository.findByProcessType(order.getProcessType())
                .orElseThrow(() -> new EntityNotFoundException("Current process not found for type: " + order.getProcessType()));
    }

    // ✅ 공정 저장 (인터페이스에서 정의된 메서드 구현)
    public void saveProcess(Process process) {
        processRepository.save(process);
    }

    // ✅ 다음 공정으로 이동 (인터페이스에서 정의된 메서드 구현)
    public void moveToNextProcess(ProductionOrder order) {
        ProcessType nextProcessType = getNextProcessType(order.getProcessType());
        if (nextProcessType != null) {
            assignToProcess(order, nextProcessType);
        } else {
            order.setOrderStatus(OrderStatus.COMPLETED);
            order.setEndDate(LocalDate.now());
            orderRepository.save(order);
        }
    }
}
