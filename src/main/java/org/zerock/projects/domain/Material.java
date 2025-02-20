package org.zerock.projects.domain;

import lombok.*;
import org.zerock.projects.domain.machines.Process;
import org.zerock.projects.domain.machines.ProcessType;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mid;            // 자재 ID

    private String mname;           // 자재명

    private String mcategory;       // 카테고리

    @Enumerated(EnumType.STRING)
    private ProcessType mprocess;        // 소요부문

    private Integer mquantity;          // 수량

    private Double mprice;          // 가격

    private String mwarehouse;       // 자재창고위치

    private String mstockstatus;    // 재고 상태
}
