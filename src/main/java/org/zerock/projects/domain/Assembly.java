package org.zerock.projects.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Getter
@Builder
public class Assembly {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long Ano; // 의장 공정 고유값
    private Boolean AtE; // 엔진 조립 유무 (true: 조립, false: 미조립)
    private Boolean SM; // 시트 장착 유무 (true: 장착, false: 미장착)
}
