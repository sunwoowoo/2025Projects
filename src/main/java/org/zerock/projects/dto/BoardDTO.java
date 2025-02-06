package org.zerock.projects.dto;

import lombok.*;
import org.zerock.projects.domain.Assembly;
import org.zerock.projects.domain.Painting;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
//   프레스 , 차제 , 도장 , 조립  의 통합 테이블
public class BoardDTO {

    private Long id;

    private Assembly assembly; // 조립
    private Painting painting; // 도장
    private String progress;  // 진행율
    private String cf;  // 결합 여부


}
