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

            // Assign the task to a machine and worker
            TaskAssignment assignment = new TaskAssignment();
            assignment.setTask(task);
            assignment.setMachine(getMachineForTaskType(taskType));
            assignment.setWorker(null);
            assignmentRepository.save(assignment);
        }
    }

    private List<TaskType> getTaskTypesForProcess(ProcessType processType) {
        // Define which TaskTypes are associated with each ProcessType
        switch (processType) {
            case PRESSING:
                return Arrays.asList(TaskType.SHEARING, TaskType.BENDING);
            case WELDING:
                return Arrays.asList(TaskType.FORMING);
            case PAINTING:
                return Arrays.asList(TaskType.DRAWING);
            case ASSEMBLY:
                return Arrays.asList(TaskType.SQUEEZING);
            case COMPLETED:
                return Collections.emptyList(); // No tasks for completed process
            default:
                throw new IllegalArgumentException("Unknown process type: " + processType);
        }
    }

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
            default:
                throw new IllegalArgumentException("Unknown task type: " + taskType);
        }
    }
    // Other CRUD methods

}
