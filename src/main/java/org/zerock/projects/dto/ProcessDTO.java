package org.zerock.projects.dto;

import lombok.Builder;
import lombok.Data;
import org.zerock.projects.domain.machines.Process;
import org.zerock.projects.domain.machines.ProcessType;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
public class ProcessDTO {
    private Long id;
    private ProcessType type;
    private boolean completed;
    private List<TaskDTO> tasks;
    private Long productionOrderId;

    public static ProcessDTO fromEntity(Process process) {
        ProcessDTO dto = ProcessDTO.builder()
                .id(process.getId())
                .type(process.getType())
                .completed(process.isCompleted())
                .productionOrderId(process.getProductionOrder().getId())
                .tasks(process.getTasks().stream().map(TaskDTO::fromEntity).collect(Collectors.toList()))
                .build();
        return dto;
    }
}
