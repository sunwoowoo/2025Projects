package org.zerock.projects.domain;

import lombok.*;
import org.zerock.projects.domain.machines.Process;
import org.zerock.projects.domain.machines.ProcessType;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ProductionOrder {      // 들어온 주문
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 차량 모델
    private String carModel;
    // 수량
    private int quantity;
    // 생산시작날짜
    private LocalDate startDate;
    // 생산완료날짜
    private LocalDate endDate;
    //수주날짜
    private LocalDate regDate;
    // 진행율
    private double progress;
    // 주문제품상태 (대기, 제작중, 완료)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;    // PENDING, IN_PROGRESS, COMPLETED
    // 제작진행상황 ( 프레싱, 용접, 도장, 조립)
    @Enumerated(EnumType.STRING)
    private ProcessType processType; // Track the current process (e.g., Pressing, Welding, Painting, Assembling)

    @OneToMany(mappedBy = "productionOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Process> processes = new ArrayList<>();

    public ProductionOrder(Long id, String carModel, int quantity, OrderStatus orderStatus){
        this.id=id;
        this.carModel=carModel;
        this.quantity = quantity;
        this.orderStatus = orderStatus;
    }
}

