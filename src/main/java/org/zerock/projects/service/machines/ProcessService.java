package org.zerock.projects.service.machines;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.zerock.projects.domain.OrderStatus;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.*;
import org.zerock.projects.domain.machines.Process;
import org.zerock.projects.repository.ProductionOrderRepository;
import org.zerock.projects.repository.machines.ProcessRepository;
import org.zerock.projects.repository.machines.TaskRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

@Service
public class ProcessService implements IProcessService {

    private final ProcessRepository processRepository;
    private final TaskRepository taskRepository;
    private final ProductionOrderRepository orderRepository;

    @Autowired
    public ProcessService(
            ProcessRepository processRepository,
            @Lazy TaskRepository taskRepository,
            ProductionOrderRepository orderRepository) {
        this.processRepository = processRepository;
        this.taskRepository = taskRepository;
        this.orderRepository = orderRepository;
    }

    //  모든 공정단계 찾기
    public List<Process> getAllProcesses() {
        return processRepository.findAll();
    }

    public void saveProcess(Process process) {

    }
}
