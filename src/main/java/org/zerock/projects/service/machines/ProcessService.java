package org.zerock.projects.service.machines;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.projects.domain.OrderStatus;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.Process;
import org.zerock.projects.domain.machines.ProcessType;
import org.zerock.projects.repository.ProductionOrderRepository;
import org.zerock.projects.repository.machines.ProcessRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProcessService {
    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProductionOrderRepository orderRepository;

    // 모든 공정단계 찾기
    public List<Process> getAllProcesses() {
        return processRepository.findAll();
    }

    // 공정 시작
    public void assignToProcess(ProductionOrder order, ProcessType processType) {
        Process process = processRepository.findByProcessType(processType);

        //공정단계가 null(주문이 대기상태 시)일 때
        if (process == null && order.getOrderStatus() == OrderStatus.PENDING) {
            // 프레싱 공정 시작(제 1단계 공정)
            process = new Process();
            process.setProcessType(ProcessType.PRESSING);
            process = processRepository.save(process);
        } else if (process == null) {       // 공정단계가 없는데 주문제품 상태가 '제작 중'으로 뜰 때 오류
            throw new IllegalArgumentException("Invalid process type: " + processType);
        }

        // Assign raw materials, components, and workers
        // 원자재, 부품, 직원, 설비 투입
        taskService.createTasksForProcess(order, process);

        if (processType == ProcessType.ASSEMBLY) {  // 마지막 공정 ASSEMBLY(조립) 단계 완료 시
            order.setOrderStatus(OrderStatus.COMPLETED);    // 주문 상태 : 완성(COMPLETED)
            order.setEndDate(LocalDate.now());          // 완성 날짜
        } else {
            order.setOrderStatus(OrderStatus.IN_PROGRESS);
        }
        order.setProcessType(processType);
        order.setStartDate(LocalDate.now());
        orderRepository.save(order);
    }
}
