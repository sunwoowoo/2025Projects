package org.zerock.projects.repository.machines;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.projects.domain.machines.Machine;
import org.zerock.projects.domain.machines.MachineType;

public interface MachineRepository extends JpaRepository<Machine, Long> {
    // 설비 종류별로 찾기 (프레서, 용접기, 도장기, 조립기)
    Machine findByMachineType(MachineType machineType);
}
