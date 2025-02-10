package org.zerock.projects.service.machines;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.projects.domain.machines.Process;
import org.zerock.projects.domain.machines.Task;
import org.zerock.projects.repository.machines.TaskRepository;
import org.zerock.projects.domain.machines.TaskType;

import java.util.List;
import java.util.Random;

@Log4j2
@Component
public class ManufacturingSimulator {
    @Autowired
    private TaskRepository taskRepository;

    @Transactional
    public void updateTaskProgress(Task task) {
        log.info("Starting updateTaskProgress for task type: {}, current progress: {}",
                task.getTaskType(), task.getProgress());

        // Add randomization to progress increment
        Random random = new Random();
        int increment = random.nextInt(15);
        int newProgress = Math.min(task.getProgress() + increment, 100);

        log.info("Increment: {}, New progress: {}", increment, newProgress);

        task.setProgress(newProgress);

        if (newProgress == 100) {
            log.info("Task reached 100%. Marking as completed.");
            task.setCompleted(true);
            taskRepository.save(task);  // Save the completed task first

            if (task.getTaskType() != TaskType.COMPLETED) {
                // Find next task in sequence
                Process process = task.getProcess();
                List<TaskType> processTaskTypes = TaskType.getTasksForProcess(process.getType());
                int currentIndex = processTaskTypes.indexOf(task.getTaskType());

                log.info("Current task index: {}, Total tasks in process: {}",
                        currentIndex, processTaskTypes.size());

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

                    log.info("New task created with ID: {}", nextTask.getId());
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
}
