package org.zerock.projects.dto;

import lombok.*;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
//  도장 테이블
public class PaintingDTO {
    private Long Pno; // 의장 공정 고유값
    private String title; // 제목
    private Boolean preprocessing; // 전처리 공정
    private Boolean electrodeposition; // 전착 공정
    private Boolean sealing;  // 실링 공정
    private Boolean MTprocesses; // 중도 도장 & 상도 도장 공정
}
