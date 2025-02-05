package org.zerock.projects.domain.machines;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PressTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private PressTaskType pressTaskType;  // 프레스 Task 종류: SHEARING, BENDING, DRAWING, FORMING, SQUEEZING
    private String description;     // 비고
    private int duration;           // Task 진행기간
    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus; //  Task 상태 : PENDING(대기), IN_PROGRESS(진행중), COMPLETED(완료)

    @ManyToOne      // 한 Process 안 많은 Task들
    @JoinColumn(name = "process_id")
    private Process process;
}
