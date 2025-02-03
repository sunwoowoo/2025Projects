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
    @Column(nullable = false)
    private String title; //  제품
    @Column(nullable = false)
    private String aa; //  진행율
    @Column(nullable = false)
    private String  bb; //  결합 요부
}
