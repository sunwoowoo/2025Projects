package org.zerock.projects.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.projects.domain.OrderStatus;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.ProcessType;
import org.zerock.projects.dto.ProcessDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductionOrderDTO {
    private Long id;
    private String carModel;
    private int quantity;
    private LocalDate regDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private OrderStatus orderStatus;
    private ProcessType processType;
    private Double progress;
    private List<ProcessDTO> processes;

    // Constructors
    public ProductionOrderDTO(String carModel, int quantity, OrderStatus orderStatus) {
        this.carModel = carModel;
        this.quantity = quantity;
        this.orderStatus = orderStatus;
    }

    // Constructor for entity conversion
    public static ProductionOrderDTO fromEntity(ProductionOrder order) {
        ProductionOrderDTO dto = ProductionOrderDTO.builder()
                .id(order.getId())
                .carModel(order.getCarModel())
                .quantity(order.getQuantity())
                .regDate(order.getRegDate())
                .startDate(order.getStartDate())
                .endDate(order.getEndDate())
                .orderStatus(order.getOrderStatus())
                .processType(order.getProcessType())
                .progress(order.getProgress())
                .processes(order.getProcesses().stream().map(ProcessDTO::fromEntity).collect(Collectors.toList()))
                .build();
        return dto;
    }
}
