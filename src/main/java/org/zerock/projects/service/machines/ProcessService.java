package org.zerock.projects.service.machines;

import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< HEAD
import org.springframework.context.annotation.Lazy;
=======
>>>>>>> 036a988685483223a9afbfa600f7d689f15189fc
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
<<<<<<< HEAD
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
=======
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
>>>>>>> 036a988685483223a9afbfa600f7d689f15189fc
    public List<Process> getAllProcesses() {
        return processRepository.findAll();
    }

<<<<<<< HEAD
    //  공정 시작
    public void assignToProcess(ProductionOrder order, ProcessType newProcessType) {
        order.setProgress(0);

=======
    // 공정 시작
    public void assignToProcess(ProductionOrder order, ProcessType newProcessType) {
        // 진행률 0%
        order.setProgress(0);

        // 주문제품 공정이 PRESSING으로 시작하지 않을 시 오류
>>>>>>> 036a988685483223a9afbfa600f7d689f15189fc
        if (order.getOrderStatus() == OrderStatus.PENDING && newProcessType != ProcessType.PRESSING) {
            throw new IllegalStateException("A PENDING order must start with PRESSING process");
        }

        Process process = processRepository.findByProcessType(newProcessType)
                .orElseGet(() -> {
<<<<<<< HEAD
=======
                    if (order.getOrderStatus() == OrderStatus.PENDING && newProcessType != ProcessType.PRESSING) {
                        throw new IllegalStateException("A PENDING order must start with PRESSING process");
                    }
>>>>>>> 036a988685483223a9afbfa600f7d689f15189fc
                    Process newProcess = new Process();
                    newProcess.setProcessType(newProcessType);
                    return processRepository.save(newProcess);
                });

<<<<<<< HEAD
        taskService.createTasksForProcess(order, process);

        if (newProcessType == ProcessType.ASSEMBLY) {
            order.setOrderStatus(OrderStatus.COMPLETED);
            order.setEndDate(LocalDate.now());
        } else {
            order.setOrderStatus(OrderStatus.IN_PROGRESS);
        }

=======
        // Assign raw materials, components, and workers
        // 원자재, 부품, 직원, 설비 투입
        taskService.createTasksForProcess(order, process);

        if (newProcessType == ProcessType.ASSEMBLY) {  // 마지막 공정 ASSEMBLY(조립) 단계 완료 시
            order.setOrderStatus(OrderStatus.IN_PROGRESS);    // 주문 상태 : 완성(COMPLETED)
            order.setEndDate(LocalDate.now());          // 완성 날짜
        } else {
            order.setOrderStatus(OrderStatus.IN_PROGRESS);
        }
>>>>>>> 036a988685483223a9afbfa600f7d689f15189fc
        order.setProcessType(newProcessType);
        order.setStartDate(LocalDate.now());
        orderRepository.save(order);
    }

<<<<<<< HEAD
    //  다음 공정 타입 가져오기
=======

>>>>>>> 036a988685483223a9afbfa600f7d689f15189fc
    public ProcessType getNextProcessType(ProcessType currentProcess) {
        switch (currentProcess) {
            case PRESSING: return ProcessType.WELDING;
            case WELDING: return ProcessType.PAINTING;
            case PAINTING: return ProcessType.ASSEMBLY;
<<<<<<< HEAD
            case ASSEMBLY: return ProcessType.COMPLETED;
=======
            case ASSEMBLY: return ProcessType.COMPLETED; // Indicates end of process
>>>>>>> 036a988685483223a9afbfa600f7d689f15189fc
            case COMPLETED: return null;
            default: throw new IllegalStateException("Invalid process: " + currentProcess);
        }
    }

<<<<<<< HEAD
    //  공정에 새로운 Task 추가
=======
>>>>>>> 036a988685483223a9afbfa600f7d689f15189fc
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

<<<<<<< HEAD
    //  현재 공정 찾기
=======
>>>>>>> 036a988685483223a9afbfa600f7d689f15189fc
    public Process getCurrentProcess(ProductionOrder order) {
        return processRepository.findByProcessType(order.getProcessType())
                .orElseThrow(() -> new EntityNotFoundException("Current process not found for type: " + order.getProcessType()));
    }
<<<<<<< HEAD

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
=======
>>>>>>> 036a988685483223a9afbfa600f7d689f15189fc
}
