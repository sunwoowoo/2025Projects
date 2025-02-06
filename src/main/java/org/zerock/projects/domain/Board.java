package org.zerock.projects.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Getter
@Builder
//   프레스 , 차제 , 도장 , 조립  의 통합 테이블
public class Board {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id; //  번호

    @ManyToOne
    @JoinColumn(name = "Ano")
    private  Assembly assembly;//  조립
    @ManyToOne
    @JoinColumn(name = "Pno")
    private  Painting painting;
    @Column(nullable = false)
    private String progress; //  진행율

    @Column(nullable = false)
    private String  cf; //  결합 요부
    public void updateProgress(){
        int percentage = 0;

        if (assembly != null) {
            if (assembly.getAtE() != null && assembly.getAtE()) {
                percentage += 50;
            }
            if (assembly.getSM() != null && assembly.getSM()) {
                percentage += 50;
            }
        }
        this.progress = percentage + "%";
    }
}
