package org.zerock.projects.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.projects.domain.machines.Machine;
import org.zerock.projects.domain.machines.MachineStatus;
import org.zerock.projects.domain.machines.MachineType;
import org.zerock.projects.repository.machines.MachineRepository;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Log4j2
public class MachineRepositoryTests {
    @Autowired
    private MachineRepository machineRepository;

    @Test
    public void testJpaInsert() {
        List<Machine> machines = new ArrayList<>();
        Machine machine = Machine.builder()
                .machineType(MachineType.PRESSER)
                .machineStatus(MachineStatus.IDLE)
                .build();
        Machine machine2 = Machine.builder()
                .machineType(MachineType.WELDER)
                .machineStatus(MachineStatus.OPERATING)
                .build();
        Machine machine3 = Machine.builder()
                .machineType(MachineType.PAINTER)
                .machineStatus(MachineStatus.MAINTENANCE)
                .build();
        Machine machine4 = Machine.builder()
                .machineType(MachineType.ASSEMBLER)
                .machineStatus(MachineStatus.BROKEN)
                .build();

        machines.add(machine);
        machines.add(machine2);
        machines.add(machine3);

        List<Machine> results = machineRepository.saveAll(machines);
    }
}
