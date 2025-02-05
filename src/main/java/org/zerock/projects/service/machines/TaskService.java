package org.zerock.projects.service.machines;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.*;
import org.zerock.projects.domain.machines.Process;
import org.zerock.projects.repository.machines.MachineRepository;
import org.zerock.projects.repository.machines.TaskAssignmentRepository;
import org.zerock.projects.repository.machines.TaskRepository;

import java.util.*;

@Service
public class TaskService {  // 공정 하위 단계인 Task 관련 Service. 수정 필요.
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAssignmentRepository assignmentRepository;

    @Autowired
    MachineRepository machineRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<PressTask> getTasksByProcess(Long processId) {
        return taskRepository.findByProcessId(processId);
    }

    public void createTasksForProcess(ProductionOrder order, Process process) {
        for (PressTaskType pressTaskType : getTaskTypesForProcess(process.getProcessType())) {
            PressTask pressTask = new PressTask();
            pressTask.setProcess(process);
            pressTask.setPressTaskType(pressTaskType);
            pressTask.setTaskStatus(TaskStatus.PENDING);
            taskRepository.save(pressTask);

            // Assign the task to a machine and worker
            TaskAssignment assignment = new TaskAssignment();
            assignment.setPressTask(pressTask);
            assignment.setMachine(getMachineForTaskType(pressTaskType));
            assignment.setWorker(null);
            assignmentRepository.save(assignment);
        }
    }

    private List<PressTaskType> getTaskTypesForProcess(ProcessType processType) {
        // Define which TaskTypes are associated with each ProcessType
        switch (processType) {
            case PRESSING:
                return Arrays.asList(PressTaskType.SHEARING, PressTaskType.BENDING);
            case WELDING:
                return Arrays.asList(PressTaskType.FORMING);
            case PAINTING:
                return Arrays.asList(PressTaskType.DRAWING);
            case ASSEMBLY:
                return Arrays.asList(PressTaskType.SQUEEZING);
            default:
                throw new IllegalArgumentException("Unknown process type: " + processType);
        }
    }

    private Machine getMachineForTaskType(PressTaskType pressTaskType) {
        // Logic to determine which machine to use for each task type
        switch (pressTaskType) {
            case SHEARING:
            case BENDING:
                return machineRepository.findByMachineType(MachineType.PRESSER);
            case FORMING:
                return machineRepository.findByMachineType(MachineType.WELDER);
            case DRAWING:
                return machineRepository.findByMachineType(MachineType.PAINTER);
            case SQUEEZING:
                return machineRepository.findByMachineType(MachineType.ASSEMBLER);
            default:
                throw new IllegalArgumentException("Unknown task type: " + pressTaskType);
        }
    }
    // Other CRUD methods
}
