package org.zerock.projects.service.machines;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.zerock.projects.domain.machines.Machine;
import org.zerock.projects.domain.machines.MachineStatus;
import org.zerock.projects.repository.machines.MachineRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;


@Service
public class MachineSimulator { // 공장설비 가동. 수정 필요
    @Autowired
    private MachineRepository machineRepository;

    @Scheduled(fixedRate = 5000) // Run every 5 seconds
    public void simulateMachineStatus() {   // 수정 필요
        List<Machine> machines = machineRepository.findAll();
        for (Machine machine : machines) {
            // Randomly update machine status
            MachineStatus[] statuses = {MachineStatus.IDLE, MachineStatus.OPERATING, MachineStatus.MAINTENANCE, MachineStatus.BROKEN};
            MachineStatus newStatus = statuses[new Random().nextInt(statuses.length)];
            machine.setMachineStatus(newStatus);
            machineRepository.save(machine);
        }
    }
}
