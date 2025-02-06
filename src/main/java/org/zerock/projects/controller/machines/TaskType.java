package org.zerock.projects.controller.machines;

public enum TaskType {
    // 프레스 공정 하위 Task들
    SHEARING, BENDING, DRAWING, FORMING, SQUEEZING,

    // 차체용접 tasks
    SPOT_WELDING, ARC_WELDING, SEAM_WELDING,

    // 도장 tasks
    SURFACE_PREPARATION, PRIMER_APPLICATION, COLOR_COATING, CLEAR_COATING,

    // 조립 tasks
    COMPONENT_FITTING, FASTENING, ELECTRICAL_WIRING, QUALITY_CHECK,

    COMPLETED
}
