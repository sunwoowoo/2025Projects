package org.zerock.projects.domain.machines;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Machine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private MachineType machineType;  // 설비 종류 : 프레서(PRESSER), 용접기(WELDER), 도장기(PAINTER), 조립기(ASSEMBLER)
    @Enumerated(EnumType.STRING)
    private MachineStatus machineStatus;    // 설비 상태 : 대기(IDLE), 가동 중(OPERATING), 점검 중(MAINTENANCE), 고장(BROKEN)
}
