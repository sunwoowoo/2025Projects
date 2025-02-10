package org.zerock.projects.dto;

import lombok.Builder;
import lombok.Data;
import org.zerock.projects.domain.OrderStatus;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.ProcessType;
import org.zerock.projects.dto.ProcessDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
public class ProductionOrderDTO {
    private Long id;
    private String carModel;
    private int quantity;
    private LocalDate startDate;
    private LocalDate endDate;
    private OrderStatus orderStatus;
    private ProcessType processType;
    private double progress;
    private List<ProcessDTO> processes;

    // Constructor for entity conversion
    public static ProductionOrderDTO fromEntity(ProductionOrder order) {
        ProductionOrderDTO dto = ProductionOrderDTO.builder()
                .id(order.getId())
                .carModel(order.getCarModel())
                .quantity(order.getQuantity())
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
