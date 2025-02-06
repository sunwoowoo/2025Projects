package org.zerock.projects.service.machines;

import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< HEAD
import org.springframework.context.annotation.Lazy;
=======
>>>>>>> 036a988685483223a9afbfa600f7d689f15189fc
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.*;
import org.zerock.projects.domain.machines.Process;
import org.zerock.projects.repository.ProductionOrderRepository;
import org.zerock.projects.repository.machines.MachineRepository;
import org.zerock.projects.repository.machines.TaskAssignmentRepository;
import org.zerock.projects.repository.machines.TaskRepository;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Service
<<<<<<< HEAD
public class TaskService {

    private final IProcessService processService;  //  수정 완료
    private final TaskRepository taskRepository;
    private final TaskAssignmentRepository assignmentRepository;
    private final MachineRepository machineRepository;
    private final ProductionOrderRepository productionOrderRepository;

    @Autowired
    public TaskService(
            @Lazy  IProcessService processService,
            TaskRepository taskRepository,
            TaskAssignmentRepository assignmentRepository,
            MachineRepository machineRepository,
            ProductionOrderRepository productionOrderRepository) {

        this.processService = processService;
        this.taskRepository = taskRepository;
        this.assignmentRepository = assignmentRepository;
        this.machineRepository = machineRepository;
        this.productionOrderRepository = productionOrderRepository;
=======
public class TaskService {  // 공정 하위 단계인 Task 관련 Service. 수정 필요.
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAssignmentRepository assignmentRepository;

    @Autowired
    MachineRepository machineRepository;

    @Autowired
    private ProcessService processService;

    @Autowired
    private ProductionOrderRepository productionOrderRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
>>>>>>> 036a988685483223a9afbfa600f7d689f15189fc
    }

    public List<Task> getTasksByProcess(Long processId) {
        return taskRepository.findByProcessId(processId);
    }

    public void createTasksForProcess(ProductionOrder order, Process process) {
        for (TaskType taskType : getTaskTypesForProcess(process.getProcessType())) {
            Task task = new Task();
            task.setProcess(process);
            task.setTaskType(taskType);
            task.setTaskStatus(TaskStatus.PENDING);
            taskRepository.save(task);

<<<<<<< HEAD
            // 기계 할당 시 예외 처리 추가
            Machine machine = machineRepository.findByMachineType(getMachineTypeForTask(taskType));
            if (machine == null) {
                throw new EntityNotFoundException("Machine not found for task type: " + taskType);
            }

            // Assign the task to a machine and worker
            TaskAssignment assignment = new TaskAssignment();
            assignment.setTask(task);
            assignment.setMachine(machine);
=======
            // Assign the task to a machine and worker
            TaskAssignment assignment = new TaskAssignment();
            assignment.setTask(task);
            assignment.setMachine(getMachineForTaskType(taskType));
>>>>>>> 036a988685483223a9afbfa600f7d689f15189fc
            assignment.setWorker(null);
            assignmentRepository.save(assignment);
        }
    }

    private List<TaskType> getTaskTypesForProcess(ProcessType processType) {
        switch (processType) {
            case PRESSING:
                return Arrays.asList(TaskType.SHEARING, TaskType.BENDING, TaskType.DRAWING, TaskType.FORMING, TaskType.SQUEEZING);
            case WELDING:
                return Arrays.asList(TaskType.SPOT_WELDING, TaskType.ARC_WELDING, TaskType.SEAM_WELDING);
            case PAINTING:
                return Arrays.asList(TaskType.SURFACE_PREPARATION, TaskType.PRIMER_APPLICATION, TaskType.COLOR_COATING, TaskType.CLEAR_COATING);
            case ASSEMBLY:
                return Arrays.asList(TaskType.COMPONENT_FITTING, TaskType.FASTENING, TaskType.ELECTRICAL_WIRING, TaskType.QUALITY_CHECK);
            case COMPLETED:
<<<<<<< HEAD
                return Collections.emptyList();
=======
                return Collections.emptyList(); // No tasks for completed process
>>>>>>> 036a988685483223a9afbfa600f7d689f15189fc
            default:
                throw new IllegalArgumentException("Unknown process type: " + processType);
        }
    }

<<<<<<< HEAD
    private MachineType getMachineTypeForTask(TaskType taskType) {
        switch (taskType) {
            case SHEARING:
            case BENDING:
                return MachineType.PRESSER;
            case FORMING:
                return MachineType.WELDER;
            case DRAWING:
                return MachineType.PAINTER;
            case SQUEEZING:
                return MachineType.ASSEMBLER;
=======

    private Machine getMachineForTaskType(TaskType taskType) {
        // Logic to determine which machine to use for each task type
        switch (taskType) {
            case SHEARING:
            case BENDING:
                return machineRepository.findByMachineType(MachineType.PRESSER);
            case FORMING:
                return machineRepository.findByMachineType(MachineType.WELDER);
            case DRAWING:
                return machineRepository.findByMachineType(MachineType.PAINTER);
            case SQUEEZING:
                return machineRepository.findByMachineType(MachineType.ASSEMBLER);
>>>>>>> 036a988685483223a9afbfa600f7d689f15189fc
            default:
                throw new IllegalArgumentException("Unknown task type: " + taskType);
        }
    }

    @Transactional
    public void updateTaskProgress(Long taskId, int progress) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));

        task.updateProgress(progress);
        taskRepository.save(task);

<<<<<<< HEAD
        //  공정(progress) 업데이트
        Process process = task.getProcess();
        process.updateProgress();
        processService.saveProcess(process);  // `IProcessService` 사용

        // 주문(progress) 업데이트
=======
        // Update the associated process
        Process process = task.getProcess();
        process.updateProgress();
        processService.saveProcess(process);

        // Update the associated production order
>>>>>>> 036a988685483223a9afbfa600f7d689f15189fc
        ProductionOrder order = process.getProductionOrder();
        order.setProgress(process.getProgress());
        productionOrderRepository.save(order);

<<<<<<< HEAD
        // 공정 완료 시 다음 단계로 이동
=======
        // Check if the process is complete and move to the next if necessary
>>>>>>> 036a988685483223a9afbfa600f7d689f15189fc
        if (process.getProgress() == 100) {
            processService.moveToNextProcess(order);
        }
    }
<<<<<<< HEAD
=======
    // Other CRUD methods

>>>>>>> 036a988685483223a9afbfa600f7d689f15189fc
}
