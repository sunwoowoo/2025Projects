package org.zerock.projects.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.projects.domain.OrderStatus;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.Process;
import org.zerock.projects.domain.machines.ProcessType;
import org.zerock.projects.domain.machines.Task;
import org.zerock.projects.repository.ProductionOrderRepository;
import org.zerock.projects.repository.machines.ProcessRepository;
import org.zerock.projects.repository.machines.TaskRepository;
import org.zerock.projects.service.machines.ProcessService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProductionOrderService {
    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private ProductionOrderRepository orderRepository;

    @Autowired
    private ProcessService processService;

    @Autowired
    private TaskRepository taskRepository;

    // 주문제품 id로 품목 하나 찾기
    public Optional<ProductionOrder> getOneOrder(Long id) {
        return orderRepository.findById(id);
    }

    // 주문제품 전부 불러오기
    public List<ProductionOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    // 주문제품 추가(생성) - 새로 들어온 차량모델 한 종류, 수량 생성
    public ProductionOrder createOrder(String carModel, int quantity) {
        if (carModel == null || carModel.isEmpty()) {
            throw new IllegalArgumentException("Car model cannot be null or empty");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        ProductionOrder order = ProductionOrder.builder()
                .carModel(carModel)
                .quantity(quantity)
                .orderStatus(OrderStatus.PENDING)
                .build();
        return orderRepository.save(order);
    }

    // 다음 제작진행으로 넘기기
    public void moveToNextProcess(ProductionOrder order) {
        ProcessType nextProcess = processService.getNextProcessType(order.getProcessType());
        if (nextProcess != null) {
            processService.assignToProcess(order, nextProcess);
            updateOrderStatus(order);
        } else {
            completeOrder(order);
            orderRepository.save(order);
        }

    }

    // 제작 과정 상태 완료 표시
    public void updateOrderStatus(ProductionOrder order) {
        Process currentProcess = processService.getCurrentProcess(order);
        if (currentProcess != null) {
            order.setProgress(currentProcess.getProgress());
            if (currentProcess.getProgress() == 100) {
                moveToNextProcess(order);
            }
        }
        if (order.getProcessType() == ProcessType.COMPLETED && order.getProgress() == 100) {
            completeOrder(order);
        } else {
            order.setEndDate(null); // Clear end date if not completed
        }
        orderRepository.save(order);
    }

    // 주문 제작 완료
    private void completeOrder(ProductionOrder order) {
        order.setOrderStatus(OrderStatus.COMPLETED);
        order.setProcessType(ProcessType.COMPLETED);
        order.setProgress(100);
        order.setEndDate(LocalDate.now());
        orderRepository.save(order);
    }

    // 진행률 갱신
    public void updateProgress(Long orderId, Long taskId, int progress) {
        ProductionOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        Process currentProcess = processService.getCurrentProcess(order);
        if (currentProcess != null) {
            Task task = currentProcess.getTasks().stream()
                    .filter(t -> t.getId().equals(taskId))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Task not found"));
            task.updateProgress(progress);
            taskRepository.save(task);
            updateOrderStatus(order);
        }
    }

    // 제작 중인 주문들 불러오기
    public List<ProductionOrder> getOrdersInProgress() {
        return orderRepository.findByOrderStatus(OrderStatus.IN_PROGRESS);
    }
}

