package org.zerock.projects.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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

    private Integer mquantity;          // 수량

    private Double mprice;          // 가격

    private String mwarehouse;       // 자재창고위치

    private String mstockstatus;    // 재고 상태
}
