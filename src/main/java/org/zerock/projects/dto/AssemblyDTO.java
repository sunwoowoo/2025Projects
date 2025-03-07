package org.zerock.projects.dto;

import lombok.*;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
//  조립  테이블
public class AssemblyDTO  {

    private Long Ano; // 의장 공정 고유값
    private String title; //  제목
    private Boolean AtE; // 엔진 조립 유무 (true: 조립, false: 미조립)
    private Boolean SM; // 시트 장착 유무 (true: 장착, false: 미장착)
}
