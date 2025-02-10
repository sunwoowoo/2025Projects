package org.zerock.projects.dto;

import lombok.*;
import org.zerock.projects.domain.Material;
import org.zerock.projects.domain.ProductionOrder;

import java.util.stream.Collectors;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MaterialDTO {
    private String mid;
    private String mname;
    private String mcategory;
    private double mquantity;
    private double mprice;
    private String mwarehouse;
    private String mstockstatus;

    public static MaterialDTO fromEntity(Material material) {
        return MaterialDTO.builder()
                .mid(material.getMid())
                .mname(material.getMname())
                .mcategory(material.getMcategory())
                .mquantity(material.getMquantity())
                .mwarehouse(material.getMwarehouse())
                .mprice(material.getMprice())
                .mstockstatus(material.getMstockstatus())
                .build();
    }

}
