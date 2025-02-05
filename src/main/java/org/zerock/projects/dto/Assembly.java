package org.zerock.projects.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Assembly {

    private Long Ano; // 의장 공정 고유값
    private Boolean AtE; // 엔진 조립 유무 (true: 조립, false: 미조립)
    private Boolean SM; // 시트 장착 유무 (true: 장착, false: 미장착)
}
