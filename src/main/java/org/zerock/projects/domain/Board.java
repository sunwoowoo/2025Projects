package org.zerock.projects.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Getter
@Builder
public class Board extends BaseEntity{
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id; //  번호


    @ManyToOne
    @JoinColumn(name = "Ano")
    private  Assembly assembly;//  조립
    @Column(nullable = false)

    private String progress; //  진행율

    @Column(nullable = false)
    private String  cf; //  결합 요부
}
