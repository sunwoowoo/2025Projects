package org.zerock.projects.service;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.Process;
import org.zerock.projects.domain.machines.ProcessType;
import org.zerock.projects.domain.machines.TaskType;
import org.zerock.projects.dto.ProductionOrderDTO;
import org.zerock.projects.dto.ProductionOrderDTO;
import org.zerock.projects.repository.ProductionOrderRepository;
import org.zerock.projects.repository.machines.ProcessRepository;
import org.zerock.projects.domain.machines.TaskType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Log4j2
@Service
@Transactional
public class ProductionOrderService {
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private ProductionOrderRepository productionOrderRepository;

    public ProductionOrderDTO readOne(Long orderId){
        ProductionOrder order = productionOrderRepository.findById(orderId)
                .orElseThrow(()-> new RuntimeException("Order not found with ID:"+ orderId));

        return ProductionOrderDTO.fromEntity(order);
    }

    private void createTasksForProcess(Process process) {
        List<TaskType> taskTypes = TaskType.getTasksForProcess(process);

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

    public Page<ProductionOrder> getAllOrders(Pageable pageable) {
        return productionOrderRepository.findAll(pageable);
    }

    // 주문 삭제
    @Transactional
    public void removeOrder(Long id) {
         productionOrderRepository.deleteById(id);
    }

    //팝업주문 저장
    public void saveOrder(ProductionOrder productionOrder){
        productionOrderRepository.save(productionOrder);
    }

    // 수정페이지 save 메소드 추가
    public ProductionOrder save(ProductionOrder order) {
        return productionOrderRepository.save(order);  // JpaRepository의 save 메소드 사용
    }

    // 주문 ID로 주문 조회
    public ProductionOrder getOrderById(Long id) {
        return productionOrderRepository.findById(id).orElse(null);  // ID에 해당하는 주문을 찾고, 없으면 null 반환
    }

    @Transactional
    public void updateOrder(Long orderId, ProductionOrderDTO orderDTO) {
        // 주문을 ID로 찾아오기
        ProductionOrder order = productionOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found:"+ orderId));

        // 주문 정보 업데이트 (carModel , quantity)
        order.setCarModel(orderDTO.getCarModel());
        order.setQuantity(orderDTO.getQuantity());

    }
}
