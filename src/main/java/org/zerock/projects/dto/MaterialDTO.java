package org.zerock.projects.dto;

import lombok.*;
import org.zerock.projects.domain.Material;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.ProcessType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.stream.Collectors;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MaterialDTO {
    private Long mid;
    private String mname;
    private String mcategory;
    @Enumerated(EnumType.STRING)
    private ProcessType mprocess;
    private Integer mquantity;
    private Double mprice;
    private String mwarehouse;
    private String mstockstatus;

    public static MaterialDTO fromEntity2(Material material) {
        return MaterialDTO.builder()
                .mid(material.getMid())
                .mname(material.getMname())
                .mcategory(material.getMcategory())
                .mprocess(material.getMprocess())
                .mquantity(material.getMquantity())
                .mwarehouse(material.getMwarehouse())
                .mprice(material.getMprice())
                .mstockstatus(material.getMstockstatus())
                .build();
    }

}
