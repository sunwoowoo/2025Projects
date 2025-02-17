package org.zerock.projects.domain.machines;

import java.util.List;

public enum TaskType {
    // 프레스 공정 하위 Task들
    SHEARING, BENDING, DRAWING, FORMING, SQUEEZING,

    // 차체용접 tasks
    SPOT_WELDING, ARC_WELDING, SEAM_WELDING,

    // 도장 tasks
    SURFACE_PREPARATION, PRIMER_APPLICATION, COLOR_COATING, CLEAR_COATING,

    // 조립 tasks
    COMPONENT_FITTING, FASTENING, ELECTRICAL_WIRING, QUALITY_CHECK,

    COMPLETED;

    public static List<TaskType> getTasksForProcess(Process process) {
        if (process == null || process.getType() == null) {
            throw new IllegalArgumentException("Process or ProcessType is null");
        }
        ProcessType processType = process.getType();
        switch (processType) {
            case PRESSING:
                return List.of(SHEARING, BENDING, DRAWING, FORMING, SQUEEZING);
            case WELDING:
                return List.of(SPOT_WELDING, ARC_WELDING, SEAM_WELDING);
            case PAINTING:
                return List.of(SURFACE_PREPARATION, PRIMER_APPLICATION, COLOR_COATING, CLEAR_COATING);
            case ASSEMBLING:
                return List.of(COMPONENT_FITTING, FASTENING, ELECTRICAL_WIRING, QUALITY_CHECK);
            default:
                throw new IllegalArgumentException("Unknown process type: " + processType);
        }
    }

    public static boolean belongsToProcess(TaskType taskType, Process process) {
        return getTasksForProcess(process).contains(taskType);
    }
}
