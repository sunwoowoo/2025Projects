package org.zerock.projects.dto;

import lombok.Builder;
import lombok.Data;
import org.zerock.projects.domain.machines.Task;
import org.zerock.projects.domain.machines.TaskType;

@Data
@Builder
public class TaskDTO {
    private Long id;
    private TaskType type;
    private int progress;
    private boolean completed;
    private Long processId; // Instead of full object, just keep the ID

    public static TaskDTO fromEntity(Task task) {
        TaskDTO dto = TaskDTO.builder()
                .id(task.getId())
                .type(task.getTaskType())
                .progress(task.getProgress())
                .completed(task.isCompleted())
                .processId(task.getProcess().getId())
                .build();
        return dto;
    }
}
