package org.zerock.projects.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.Process;
import org.zerock.projects.domain.machines.ProcessType;
import org.zerock.projects.domain.machines.TaskType;
import org.zerock.projects.repository.ProductionOrderRepository;
import org.zerock.projects.repository.machines.ProcessRepository;

import java.util.List;

@Log4j2
@Service
@Transactional
public class ProductionOrderService {
    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private ProductionOrderRepository productionOrderRepository;

    private void createTasksForProcess(Process process) {
        List<TaskType> taskTypes = TaskType.getTasksForProcess(process.getType());

        for (TaskType taskType : taskTypes) {
            process.addTask(taskType);
        }

        processRepository.save(process);
    }

    // Add validation method
    public void validateOrderProgress(ProductionOrder order) {
        Process currentProcess = processRepository.findByProductionOrderAndType(order, order.getProcessType());

        if (currentProcess == null) {
            throw new IllegalStateException("Current process not found");
        }

        // Validate that previous processes are completed
        for (ProcessType type : ProcessType.values()) {
            if (type.ordinal() < order.getProcessType().ordinal()) {
                Process previousProcess = processRepository.findByProductionOrderAndType(order, type);
                if (previousProcess == null || !previousProcess.isCompleted()) {
                    throw new IllegalStateException(
                            "Previous process " + type + " must be completed before moving to " +
                                    order.getProcessType()
                    );
                }
            }
        }
    }

    public List<ProductionOrder> getAllOrders() {
        List<ProductionOrder> result = productionOrderRepository.findAll();
        result.forEach(order -> log.info(order));
        return result;
    }
}
