package org.zerock.projects.service.machines;

import org.springframework.beans.factory.annotation.Autowired;
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
public class ProcessService {
    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProductionOrderRepository orderRepository;

    // 모든 공정단계 찾기
    public List<Process> getAllProcesses() {
        return processRepository.findAll();
    }

    // 공정 시작
    public void assignToProcess(ProductionOrder order, ProcessType newProcessType) {
        // 진행률 0%
        order.setProgress(0);

        // 주문제품 공정이 PRESSING으로 시작하지 않을 시 오류
        if (order.getOrderStatus() == OrderStatus.PENDING && newProcessType != ProcessType.PRESSING) {
            throw new IllegalStateException("A PENDING order must start with PRESSING process");
        }

        Process process = processRepository.findByProcessType(newProcessType)
                .orElseGet(() -> {
                    if (order.getOrderStatus() == OrderStatus.PENDING && newProcessType != ProcessType.PRESSING) {
                        throw new IllegalStateException("A PENDING order must start with PRESSING process");
                    }
                    Process newProcess = new Process();
                    newProcess.setProcessType(newProcessType);
                    return processRepository.save(newProcess);
                });

        // Assign raw materials, components, and workers
        // 원자재, 부품, 직원, 설비 투입
        taskService.createTasksForProcess(order, process);

        if (newProcessType == ProcessType.ASSEMBLY) {  // 마지막 공정 ASSEMBLY(조립) 단계 완료 시
            order.setOrderStatus(OrderStatus.IN_PROGRESS);    // 주문 상태 : 완성(COMPLETED)
            order.setEndDate(LocalDate.now());          // 완성 날짜
        } else {
            order.setOrderStatus(OrderStatus.IN_PROGRESS);
        }
        order.setProcessType(newProcessType);
        order.setStartDate(LocalDate.now());
        orderRepository.save(order);
    }


    public ProcessType getNextProcessType(ProcessType currentProcess) {
        switch (currentProcess) {
            case PRESSING: return ProcessType.WELDING;
            case WELDING: return ProcessType.PAINTING;
            case PAINTING: return ProcessType.ASSEMBLY;
            case ASSEMBLY: return ProcessType.COMPLETED; // Indicates end of process
            case COMPLETED: return null;
            default: throw new IllegalStateException("Invalid process: " + currentProcess);
        }
    }

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

    public Process getCurrentProcess(ProductionOrder order) {
        return processRepository.findByProcessType(order.getProcessType())
                .orElseThrow(() -> new EntityNotFoundException("Current process not found for type: " + order.getProcessType()));
    }
}
