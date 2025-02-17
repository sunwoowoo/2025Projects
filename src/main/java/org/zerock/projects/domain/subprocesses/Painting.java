package org.zerock.projects.domain.subprocesses;

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
//  도장 테이블
public class Painting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Pno; // 의장 공정 고유값
    private String title; // 제목
    private Boolean preprocessing; // 전처리 공정
    private Boolean electrodeposition; // 전착 공정
    private Boolean sealing;  // 실링 공정
    private Boolean MTprocesses; // 중도 도장 & 상도 도장 공정

    public void setPreprocessing(boolean preprocessing) {
        this.preprocessing = preprocessing;
    }

    public void setElectrodeposition(boolean electrodeposition) {
        this.electrodeposition = electrodeposition;
    }

    public void setSealing(boolean sealing) {
        this.sealing = sealing;
    }

    public void setMTprocesses(boolean mtprocesses) {
        this.MTprocesses = mtprocesses;
    }
}
